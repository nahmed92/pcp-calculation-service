package com.deltadental.pcp.calculation.interservice;

import static com.deltadental.pcp.config.interservice.pojo.PCPConfigServiceConstants.CLAIM_STATUS;
import static com.deltadental.pcp.config.interservice.pojo.PCPConfigServiceConstants.EXPLANATION_CODE;
import static com.deltadental.pcp.config.interservice.pojo.PCPConfigServiceConstants.PROCEDURE_CODE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.config.interservice.pojo.GroupRestrictions;
import com.deltadental.pcp.config.interservice.pojo.InclusionExclusion;
import com.deltadental.pcp.config.interservice.pojo.PcpConfigResponse;
import com.deltadental.pcp.interservice.PCPConfigServiceClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component("pcpConfigData")
@RefreshScope
@Slf4j
public class PCPConfigData implements InitializingBean {

	@Value("${pcp.wash.rule.cutoff.day}")
	private Integer washRuleCutoffDay;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PCPConfigServiceClient pcpConfigServiceClient;

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S", Locale.US);
	private final DateFormat mmddyyyyFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
	private static final String ZONE_ID = "America/Los_Angeles";
	
	private List<PcpConfigResponse> claimStatusList = new ArrayList<>();
	private List<PcpConfigResponse> procedureCodes = new ArrayList<>();
	private List<PcpConfigResponse> explanationCodes = new ArrayList<>();
	private String providerLookAHeadDays = "";

	@Override
    public void afterPropertiesSet() throws Exception {
		log.info("START PCPConfigData.afterPropertiesSet");
		claimStatusList.clear();
		procedureCodes.clear();
		explanationCodes.clear();
		log.info("Cleared all the pcp config data!");
		claimStatuses();
		log.info("Claim status  : {} ",claimStatusList);
		explanationCodes();
		log.info("Explanation codes : {} ",explanationCodes);
		procedureCodes();
		log.info("Procedure codes : {} ",procedureCodes);
		log.info("Wash rule cutoff day {} ",washRuleCutoffDay);
		log.info("PCP Effectice Date {} ",calculatePCPEffectiveDate());
		setProviderLookAHeadDays(fetchProviderLookAHeadDays());
		log.info("Provider Look A Head Days {}", providerLookAHeadDays);
		log.info("END PCPConfigData.afterPropertiesSet");
	}
	
	@Scheduled(cron = "${pcp.config.data.refresh.corn.expression}", zone = ZONE_ID)
	@Synchronized
	public void refreshPCPConfigData() {
		log.info("START PCPConfigData.refreshPCPConfigData");
		try {
			afterPropertiesSet();
			log.info("Config data refreshed time {}", new Date());
		} catch (Exception e) {
			log.error("Unable to refresh pcp config",e);
		}
		log.info("START PCPConfigData.refreshPCPConfigData");
	}

	private List<PcpConfigResponse> getPcpConfigResponseList(String jsonString) {
		try {
			JsonNode jsonNode = objectMapper.readTree(jsonString);
			List<PcpConfigResponse> explnCodesList = objectMapper.convertValue(jsonNode,
					new TypeReference<List<PcpConfigResponse>>() {
					});
			return explnCodesList;
		} catch (Exception e) {
			return new ArrayList<PcpConfigResponse>();
		}
	}

	private void claimStatuses() {
		log.info("START PCPConfigData.claimStatuses");
		String jsonClaimStatusStr = pcpConfigServiceClient.getPCPConfigData(CLAIM_STATUS);
		List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonClaimStatusStr);
		setClaimStatusList(pcpConfigResponses);
		log.info("END PCPConfigData.claimStatuses");
	}

	private void explanationCodes() {
		log.info("START PCPConfigData.explanationCodes");
		String jsonExplanationCodeStr = pcpConfigServiceClient.getPCPConfigData(EXPLANATION_CODE);
		List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonExplanationCodeStr);
		setExplanationCodes(pcpConfigResponses);
		log.info("END PCPConfigData.explanationCodes");
	}

	private void procedureCodes() {
		log.info("START PCPConfigData.procedureCodes");
		String jsonProcedureCodeStr = pcpConfigServiceClient.getPCPConfigData(PROCEDURE_CODE);
		List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonProcedureCodeStr);
		setProcedureCodes(pcpConfigResponses);
		log.info("END PCPConfigData.procedureCodes");
	}
	
	public void setProviderLookAHeadDays(String providerLookAHeadDays) {
		this.providerLookAHeadDays = providerLookAHeadDays;
	}
	
	public String providerLookAHeadDays() {
		return providerLookAHeadDays;
	}
	
	private String fetchProviderLookAHeadDays() {
		return pcpConfigServiceClient.providerLookaheadDays();
	}
	
	public boolean isClaimStatusValid(String claimStatus) {
		log.info("START PCPConfigData.isClaimStatusValid");
		boolean isClaimStatusValid = false;
		try {
			List<PcpConfigResponse> claimStatusList = this.getClaimStatusList();
			if(CollectionUtils.isNotEmpty(claimStatusList)) {
				isClaimStatusValid = claimStatusList.stream().anyMatch(pcpConfigResponse -> StringUtils.equals(pcpConfigResponse.getCodeValue(), claimStatus));
			}			
		} catch (Exception e) {
			isClaimStatusValid = false;
		}
		log.info("Claim Status Valid {} ",isClaimStatusValid);
		return isClaimStatusValid;
	}
	
	public boolean isExplanationCodeValid(List<ServiceLine> serviceLines) {
		log.info("START PCPConfigData.isExplanationCodeValid");
		boolean isExplanationCodeValid = false;
		if (CollectionUtils.isNotEmpty(serviceLines)) {
			for(ServiceLine serviceLine : serviceLines) {
				for (PcpConfigResponse pcpConfigResponse : getExplanationCodes()) {
					if( StringUtils.equals(StringUtils.trim(pcpConfigResponse.getCodeValue()), StringUtils.trim(serviceLine.getExplnCode()))) {
						isExplanationCodeValid = true;
						log.info("Explanation Code matched for {} ", serviceLine.getExplnCode());
						break;
					}
				}
				if(isExplanationCodeValid) {
					break;
				}
			}
		}
		log.info("END PCPConfigData.isExplanationCodeValid");
		return isExplanationCodeValid;
	}
	
	public boolean isProcedureCodeValid(List<ServiceLine> serviceLines) {
		log.info("START PCPConfigData.isProcedureCodeValid");
		boolean isProcedureCodeValid = true;
		if (CollectionUtils.isNotEmpty(serviceLines)) {
			for (ServiceLine serviceLine : serviceLines) {
				for (PcpConfigResponse pcpConfigResponse : getProcedureCodes()) {
					if (StringUtils.equals(StringUtils.trim(pcpConfigResponse.getCodeValue()), StringUtils.trim(serviceLine.getProcedureCode()))) {
						isProcedureCodeValid = false;
						log.info("Procedure Code matched for {} ", serviceLine.getProcedureCode());
						break;
					}
				}
				if(!isProcedureCodeValid) {
					break;
				}
			}
		}
		log.info("END PCPConfigData.isProcedureCodeValid");
		return isProcedureCodeValid;
	}
	
	public boolean isProviderInInclusionList(String providerId, String group, String division) {
		log.info("START PCPConfigData.isProviderInInclusionList()");
		Boolean inclusionFlag = Boolean.TRUE;
		if(StringUtils.isNotBlank(providerId) && StringUtils.isNotBlank(group) && StringUtils.isNotBlank(division)) {
			InclusionExclusion[] inclusions = pcpConfigServiceClient.inclusions(providerId);
			List<InclusionExclusion> inclusionList = Arrays.asList(inclusions);
			if (CollectionUtils.isNotEmpty(inclusionList)) {
				inclusionFlag = Boolean.valueOf(inclusionList.stream().anyMatch(inclusion -> matchInclusion(inclusion, providerId, group, division)));
				if(inclusionFlag.booleanValue()) {
					log.info("Provider {}, Group {}, Division {} is listed in inclusion list, inclusion flag {}.", providerId, group, division, inclusionFlag);
				} else {
					log.info("Provider {}, Group {}, Division {} is not listed in inclusion list, inclusion flag {}.", providerId, group, division, inclusionFlag);
				}
			} else {
				log.info("Provider {}, Group {}, Division {} is not listed in inclusion list, inclusion flag {}.", providerId, group, division, inclusionFlag);
			}
		}		
		log.info("END PCPConfigData.isProviderInInclusionList()");
		return inclusionFlag.booleanValue();
	}
	
	public boolean isProviderInExclusionList(String providerId, String group, String division) {
		log.info("START PCPConfigData.isProviderInExclusionList {}, {}, {}", providerId, group, division);
		Boolean providerNotexclusionFlag = Boolean.TRUE;
		if(StringUtils.isNotBlank(providerId) && StringUtils.isNotBlank(group) && StringUtils.isNotBlank(division)) {
			InclusionExclusion[] exclusions = pcpConfigServiceClient.exclusions(providerId);
			List<InclusionExclusion> exclusionList = Arrays.asList(exclusions);
			if (CollectionUtils.isNotEmpty(exclusionList)) {
				providerNotexclusionFlag = Boolean.valueOf(exclusionList.stream().anyMatch(exclusion -> matchExclusion(exclusion, providerId, group, division)));	
				if(providerNotexclusionFlag.booleanValue()) {
					log.info("Provider {}, Group {}, Division {} is not listed in exlusion list, inclusion flag {}", providerId, group, division, providerNotexclusionFlag);
				} else {
					log.info("Provider {}, Group {}, Division {} is listed in exlusion list, inclusion flag {}", providerId, group, division, providerNotexclusionFlag);
				}
			} else {
				log.info("Provider {}, Group {}, Division {} is not listed in exlusion list, inclusion flag {}", providerId, group, division, providerNotexclusionFlag);
			}
		}
		log.info("END PCPConfigData.isProviderInExclusionList {}, {}, {}", providerId, group, division);
		return providerNotexclusionFlag.booleanValue();
	}
	
	public String calculatePCPEffectiveDate() {
		log.info("START : PCPConfigData.calculatePCPEffectiveDate");
		ZoneId defaultZoneId = ZoneId.of(ZONE_ID);
		LocalDate now = LocalDate.now(defaultZoneId);		
		int currentDateDay = now.getDayOfMonth();
		String pcpEffectiveDate = null;
        if (currentDateDay < washRuleCutoffDay) {
        	LocalDate firstDayOfMonth = LocalDate.now(defaultZoneId).with(TemporalAdjusters.firstDayOfMonth());
        	Date firstDateOfMonth = Date.from(firstDayOfMonth.atStartOfDay(defaultZoneId).toInstant());
        	pcpEffectiveDate = mmddyyyyFormatter.format(firstDateOfMonth);
        } else {
        	LocalDate firstDayOfNextMonth = LocalDate.now(defaultZoneId).with(TemporalAdjusters.firstDayOfNextMonth());
        	Date firstDateOfNextMonth = Date.from(firstDayOfNextMonth.atStartOfDay(defaultZoneId).toInstant());
        	pcpEffectiveDate = mmddyyyyFormatter.format(firstDateOfNextMonth);
        }
        log.info("END : PCPConfigData.calculatePCPEffectiveDate {} !", pcpEffectiveDate);
        return pcpEffectiveDate;
	}
	
	private boolean matchInclusion(InclusionExclusion inclusionExclusion, String providerId, String group, String division) {
		log.info("START : PCPConfigData.matchInclusion");
		LocalDate effectiveDate = LocalDate.parse(inclusionExclusion.getEffectiveDate(), dateTimeFormatter);
		LocalDate now = LocalDate.now();
		boolean returnValue = false;
		if(now.isAfter(effectiveDate) || now.isEqual(effectiveDate)) {
			GroupRestrictions groupRestrictions = inclusionExclusion.getGroupRestrictions();
			returnValue = StringUtils.equals(groupRestrictions.getMasterContractId(), providerId) && StringUtils.equals(groupRestrictions.getGroupId(), group) && StringUtils.equals(groupRestrictions.getDivisionId(), division);
		}
		log.info("Returning {} for Effective date {}, provider id {}, group {}, division {} as of now {} for inclusion.",returnValue,effectiveDate, providerId, group, division, now);
		log.info("END : PCPConfigData.matchInclusion");
		return returnValue;
	}
	
	private boolean matchExclusion(InclusionExclusion inclusionExclusion, String providerId, String group, String division) {
		log.info("START : PCPConfigData.matchExclusion");
		LocalDate effectiveDate = LocalDate.parse(inclusionExclusion.getEffectiveDate(), dateTimeFormatter);
		LocalDate now = LocalDate.now();
		boolean returnValue = true;
		if(effectiveDate.isBefore(now) || now.isEqual(effectiveDate)) {
			GroupRestrictions groupRestrictions = inclusionExclusion.getGroupRestrictions();
			returnValue = !(StringUtils.equals(groupRestrictions.getMasterContractId(), providerId) && StringUtils.equals(groupRestrictions.getGroupId(), group)  && StringUtils.equals(groupRestrictions.getDivisionId(), division));
		}
		log.info("Returning {} for Effective date {}, provider id {}, group {}, division {} as of now {} for exclusion.",returnValue,effectiveDate, providerId, group, division, now);
		log.info("END : PCPConfigData.matchExclusion");
		return returnValue;
	}
}
