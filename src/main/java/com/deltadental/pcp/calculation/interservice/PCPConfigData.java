package com.deltadental.pcp.calculation.interservice;

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
@Slf4j
public class PCPConfigData implements InitializingBean {

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
		log.info("Procedure codes : ",procedureCodes);
		log.info("END PCPConfigData.afterPropertiesSet");
	}
	
	@Scheduled(cron = "* * 2 * * *", zone = ZONE_ID)
	@Synchronized
	//FIXME: remove scheduler and cache config data
	public void refreshPCPConfigData() {
		log.info("START PCPConfigData.refreshPCPConfigData");
		try {
			afterPropertiesSet();
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
		String jsonClaimStatusStr = pcpConfigServiceClient.claimStatus();
		List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonClaimStatusStr);
		setClaimStatusList(pcpConfigResponses);
	}

	private void explanationCodes() {
		String jsonExplanationCodeStr = pcpConfigServiceClient.explanationCode();
		List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonExplanationCodeStr);
		setExplanationCodes(pcpConfigResponses);
	}

	private void procedureCodes() {
		String jsonProcedureCodeStr = pcpConfigServiceClient.procedureCode();
		List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonProcedureCodeStr);
		setProcedureCodes(pcpConfigResponses);
	}
	
	
	public boolean isClaimStatusValid(String claimStatus) {
		try {
			List<PcpConfigResponse> claimStatusList = this.getClaimStatusList();
			return claimStatusList.stream().anyMatch(pcpConfigResponse -> StringUtils.equals(pcpConfigResponse.getCodeValue(), claimStatus));
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isExplanationCodeValid(List<ServiceLine> serviceLines) {
		boolean isExplanationCodeValid = false;
		if (serviceLines != null && !serviceLines.isEmpty()) {
			for(ServiceLine serviceLine : serviceLines) {
				for (PcpConfigResponse pcpConfigResponse : getExplanationCodes()) {
					if( StringUtils.equals(StringUtils.trim(pcpConfigResponse.getCodeValue()), StringUtils.trim(serviceLine.getExplnCode()))) {
						isExplanationCodeValid = true;
						break;
					}
				}
				if(isExplanationCodeValid) {
					break;
				}
			}
		}
		return isExplanationCodeValid;
	}
	
	public boolean isProcedureCodeValid(List<ServiceLine> serviceLines) {
		boolean isProcedureCodeValid = true;
		if (serviceLines != null && !serviceLines.isEmpty()) {
			for (ServiceLine serviceLine : serviceLines) {
				for (PcpConfigResponse pcpConfigResponse : getProcedureCodes()) {
					if (StringUtils.equals(StringUtils.trim(pcpConfigResponse.getCodeValue()), StringUtils.trim(serviceLine.getProcedureCode()))) {
						isProcedureCodeValid = false;
						break;
					}
				}
				if(!isProcedureCodeValid) {
					break;
				}
			}
		}
		return isProcedureCodeValid;
	}
	
	public boolean isProviderInInclusionList(String providerId, String group, String division) {
		Boolean inclusionFlag = Boolean.TRUE;
		InclusionExclusion[] inclusions = pcpConfigServiceClient.inclusions(providerId);
		List<InclusionExclusion> inclusionList = Arrays.asList(inclusions);
		if (CollectionUtils.isNotEmpty(inclusionList)) {
			if (inclusionList.size() == 1) {
				inclusionFlag = Boolean.valueOf(matchInclusion(inclusionList.get(0), providerId, group, division));
				log.info("Provider {}, Group {}, Division {} is listed in inclusion list.", providerId, group, division);
			} else {
				inclusionFlag = Boolean.valueOf(inclusionList.stream().anyMatch(inclusion -> matchInclusion(inclusion, providerId, group, division)));
				log.info("Provider {}, Group {}, Division {} is listed in inclusion list.", providerId, group, division);
			}
		} else {
			inclusionFlag = Boolean.TRUE;
			log.info("Provider {}, Group {}, Division {} is not listed in inclusion list.", providerId, group, division);
		}
		return inclusionFlag.booleanValue();
	}
	
	public boolean isProviderInExclusionList(String providerId, String group, String division) {
		Boolean exclusionFlag = Boolean.FALSE;
		InclusionExclusion[] exclusions = pcpConfigServiceClient.exclusions(providerId);
		List<InclusionExclusion> exclusionList = Arrays.asList(exclusions);
		if (CollectionUtils.isNotEmpty(exclusionList)) {
			if (exclusionList.size() == 1) {
				exclusionFlag = Boolean.valueOf(matchExclusion(exclusionList.get(0), providerId, group, division));
				if (!exclusionFlag) {
					log.info("Provider {}, Group {}, Division {} is listed in exlusion list.", providerId, group, division);
				}
			} else {
				exclusionFlag = Boolean.valueOf(exclusionList.stream().anyMatch(exclusion -> matchInclusion(exclusion, providerId, group, division)));
				log.info("Provider {}, Group {}, Division {} is listed in exlusion list.", providerId, group, division);
			}
		} else {
			log.info("Provider {}, Group {}, Division {} is not listed in exlusion list.", providerId, group, division);
			exclusionFlag = Boolean.TRUE;
		}
		return exclusionFlag;
	}
	
	public String calculatePCPEffectiveDate() {
		log.info("START : PCPConfigData.calculatePCPEffectiveDate");
		ZoneId defaultZoneId = ZoneId.of(ZONE_ID);
		LocalDate now = LocalDate.now(defaultZoneId);		
		int currentDateDay = now.getDayOfMonth();
		String pcpEffectiveDate = null;
        if (currentDateDay < 16) {
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
		LocalDate effectiveDate = LocalDate.parse(inclusionExclusion.getEffectiveDate(), dateTimeFormatter);
		LocalDate now = LocalDate.now();
		if(now.isAfter(effectiveDate) || now.isEqual(effectiveDate)) {
			GroupRestrictions groupRestrictions = inclusionExclusion.getGroupRestrictions();
			return StringUtils.equals(groupRestrictions.getMasterContractId(), providerId) && StringUtils.equals(groupRestrictions.getGroupId(), group) && StringUtils.equals(groupRestrictions.getDivisionId(), division);
		} else {
			log.info("Provider {} inclusion list configuration is not effective as of this date {}.", providerId, now);
			return true;
		}
	}
	
	private boolean matchExclusion(InclusionExclusion inclusionExclusion, String providerId, String group, String division) {
		LocalDate effectiveDate = LocalDate.parse(inclusionExclusion.getEffectiveDate(), dateTimeFormatter);
		LocalDate now = LocalDate.now();
		if(now.isBefore(effectiveDate) || now.isEqual(effectiveDate)) {
			GroupRestrictions groupRestrictions = inclusionExclusion.getGroupRestrictions();
			return !(StringUtils.equals(groupRestrictions.getMasterContractId(), providerId) && StringUtils.equals(groupRestrictions.getGroupId(), group)  && StringUtils.equals(groupRestrictions.getDivisionId(), division));
		} else {
			log.info("Provider {} exlusion list configuration is not effective as of this date {}.", providerId, now);
			return true;
		}
	}
}
