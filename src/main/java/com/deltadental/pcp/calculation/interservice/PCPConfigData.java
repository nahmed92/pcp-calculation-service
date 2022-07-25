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

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.config.interservice.pojo.GroupRestrictions;
import com.deltadental.pcp.config.interservice.pojo.InclusionExclusion;
import com.deltadental.pcp.config.interservice.pojo.PcpConfigResponse;
import com.deltadental.pcp.interservice.PCPConfigServiceClient;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Component("pcpConfigData")
@Slf4j
public class PCPConfigData {

    @Value("${pcp.wash.rule.cutoff.day}")
    private Integer washRuleCutoffDay;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PCPConfigServiceClient pcpConfigServiceClient;

    private final DateFormat MM_DD_YYYY_FORMATTER = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
    private static final String ZONE_ID = "America/Los_Angeles";

    private List<PcpConfigResponse> claimStatusList = new ArrayList<>();
    private List<PcpConfigResponse> procedureCodes = new ArrayList<>();
    private List<PcpConfigResponse> explanationCodes = new ArrayList<>();
    private String providerLookAHeadDays = "90";

    @MethodExecutionTime
    @PostConstruct
    public void afterPropertiesSet() {
        log.info("START PCPConfigData.afterPropertiesSet");
        claimStatusList.clear();
        procedureCodes.clear();
        explanationCodes.clear();
        log.info("Cleared all the pcp config data!");
        claimStatusList = getClaimStatusList();
        log.info("Claim status  : {} ", claimStatusList);
        explanationCodes = getExplanationCodes();
        log.info("Explanation codes : {} ", explanationCodes);
        procedureCodes= getProcedureCodes();
        log.info("Procedure codes : {} ", procedureCodes);
        log.info("Wash rule cutoff day {} ", washRuleCutoffDay);
        log.info("PCP Effective Date {} ", calculatePCPEffectiveDate());
        setProviderLookAHeadDays(fetchProviderLookAHeadDays());
        log.info("Provider Look A Head Days {}", providerLookAHeadDays());
        log.info("END PCPConfigData.afterPropertiesSet");
    }

    @MethodExecutionTime
    @Scheduled(initialDelayString = "${scheduling.job.pcp.config.delay}", fixedDelayString = "${scheduling.job.pcp.config.delay}")
    @Synchronized
    public void refreshPCPConfigData() {
        log.info("START PCPConfigData.refreshPCPConfigData");
        try {
            afterPropertiesSet();
            log.info("Config data refreshed time {}", new Date());
        } catch (Exception e) {
            log.error("Unable to refresh pcp config", e);
        }
        log.info("START PCPConfigData.refreshPCPConfigData");
    }

    @MethodExecutionTime
    private List<PcpConfigResponse> getPcpConfigResponseList(String jsonString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            return objectMapper.convertValue(jsonNode,
                    new TypeReference<List<PcpConfigResponse>>() {
                    });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @MethodExecutionTime
    @Cacheable(value = "claimStatusListCache")
    private List<PcpConfigResponse> getClaimStatusList() {
        log.info("START PCPConfigData.getClaimStatusList()");
        String jsonClaimStatusStr = pcpConfigServiceClient.getPCPConfigData(CLAIM_STATUS);
        List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonClaimStatusStr); 
        log.info("END PCPConfigData.getClaimStatusList()");
        return pcpConfigResponses;
    }

    @MethodExecutionTime  
    @Cacheable(value = "explanationCodesCache")
    private List<PcpConfigResponse> getExplanationCodes() {
        log.info("START PCPConfigData.getExplanationCodes()");
        String jsonExplanationCodeStr = pcpConfigServiceClient.getPCPConfigData(EXPLANATION_CODE);
        List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonExplanationCodeStr);
        log.info("END PCPConfigData.getExplanationCodes()");
        return pcpConfigResponses;               
    }

    @MethodExecutionTime
    @Cacheable(value = "procedureCodesCache")
    private List<PcpConfigResponse> getProcedureCodes() {
        log.info("START PCPConfigData.getProcedureCodes()");
        String jsonProcedureCodeStr = pcpConfigServiceClient.getPCPConfigData(PROCEDURE_CODE);
        List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonProcedureCodeStr);
        log.info("END PCPConfigData.getProcedureCodes()");
        return pcpConfigResponses;       
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

    @MethodExecutionTime
    public boolean isClaimStatusValid(String claimStatus) {
        log.info("START PCPConfigData.isClaimStatusValid");
        boolean isClaimStatusValid = false;
        try {
            List<PcpConfigResponse> claimStatusList = this.getClaimStatusList();
            if (CollectionUtils.isNotEmpty(claimStatusList)) {
                isClaimStatusValid = claimStatusList.stream().anyMatch(pcpConfigResponse -> StringUtils.equals(pcpConfigResponse.getCodeValue(), claimStatus));
            }
        } catch (Exception e) {
            log.error("Unknown exception occurred in checking claim status returning  {}", false);
        }
        log.info("Claim Status Valid {} ", isClaimStatusValid);
        return isClaimStatusValid;
    }

    @MethodExecutionTime
    public boolean isExplanationCodeValid(List<ServiceLine> serviceLines) {
        log.info("START PCPConfigData.isExplanationCodeValid");
        boolean isExplanationCodeValid = false;
        if (CollectionUtils.isNotEmpty(serviceLines)) {
            for (ServiceLine serviceLine : serviceLines) {
                for (PcpConfigResponse pcpConfigResponse : getExplanationCodes()) {
                    if (StringUtils.equals(StringUtils.trim(pcpConfigResponse.getCodeValue()), StringUtils.trim(serviceLine.getExplnCode()))) {
                        isExplanationCodeValid = true;
                        log.info("Explanation Code matched for {} ", serviceLine.getExplnCode());
                        break;
                    }
                }
                if (isExplanationCodeValid) {
                    break;
                }
            }
        }
        log.info("END PCPConfigData.isExplanationCodeValid");
        return isExplanationCodeValid;
    }

    @MethodExecutionTime
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
                if (!isProcedureCodeValid) {
                    break;
                }
            }
        }
        log.info("END PCPConfigData.isProcedureCodeValid");
        return isProcedureCodeValid;
    }

    @MethodExecutionTime
    public boolean isProviderInInclusionList(String providerId, String group, String division) {
        log.info("START PCPConfigData.isProviderInInclusionList()");
        boolean inclusionFlag = true;
        if (StringUtils.isNotBlank(providerId) && StringUtils.isNotBlank(group) && StringUtils.isNotBlank(division)) {
            InclusionExclusion[] inclusions = pcpConfigServiceClient.inclusions(providerId);
            List<InclusionExclusion> inclusionList = Arrays.asList(inclusions);
            if (CollectionUtils.isNotEmpty(inclusionList)) {
                inclusionFlag = inclusionList.stream().anyMatch(inclusion -> matchInclusion(inclusion, providerId, group, division));
            }
        }
        log.info("Provider {}, Group {}, Division {}, inclusion flag {}.", providerId, group, division, inclusionFlag);
        log.info("END PCPConfigData.isProviderInInclusionList()");
        return inclusionFlag;
    }

    @MethodExecutionTime
    public boolean isProviderInExclusionList(String providerId, String group, String division) {
        log.info("START PCPConfigData.isProviderInExclusionList");
        boolean exclusionFlag = false;
        if (StringUtils.isNotBlank(providerId) && StringUtils.isNotBlank(group) && StringUtils.isNotBlank(division)) {
            InclusionExclusion[] exclusions = pcpConfigServiceClient.exclusions(providerId);
            List<InclusionExclusion> exclusionList = Arrays.asList(exclusions);
            if (CollectionUtils.isNotEmpty(exclusionList)) {
                exclusionFlag = exclusionList.stream().anyMatch(exclusion -> matchExclusion(exclusion, providerId, group, division));
            }
        }
        log.info("Provider {}, Group {}, Division {}, exclusion flag {}", providerId, group, division, exclusionFlag);
        log.info("END PCPConfigData.isProviderInExclusionList");
        return exclusionFlag;
    }

    @MethodExecutionTime
    public String calculatePCPEffectiveDate() {
        log.info("START : PCPConfigData.calculatePCPEffectiveDate");
        ZoneId defaultZoneId = ZoneId.of(ZONE_ID);
        LocalDate now = LocalDate.now(defaultZoneId);
        int currentDateDay = now.getDayOfMonth();
        String pcpEffectiveDate;
        if (currentDateDay < washRuleCutoffDay) {
            LocalDate firstDayOfMonth = LocalDate.now(defaultZoneId).with(TemporalAdjusters.firstDayOfMonth());
            Date firstDateOfMonth = Date.from(firstDayOfMonth.atStartOfDay(defaultZoneId).toInstant());
            pcpEffectiveDate = MM_DD_YYYY_FORMATTER.format(firstDateOfMonth);
        } else {
            LocalDate firstDayOfNextMonth = LocalDate.now(defaultZoneId).with(TemporalAdjusters.firstDayOfNextMonth());
            Date firstDateOfNextMonth = Date.from(firstDayOfNextMonth.atStartOfDay(defaultZoneId).toInstant());
            pcpEffectiveDate = MM_DD_YYYY_FORMATTER.format(firstDateOfNextMonth);
        }
        log.info("END : PCPConfigData.calculatePCPEffectiveDate {} !", pcpEffectiveDate);
        return pcpEffectiveDate;
    }

    @MethodExecutionTime
    private boolean matchInclusion(InclusionExclusion inclusionExclusion, String providerId, String group, String division) {
        log.info("START : PCPConfigData.matchInclusion");
        GroupRestrictions groupRestrictions = inclusionExclusion.getGroupRestrictions();
        boolean returnValue = StringUtils.equals(groupRestrictions.getMasterContractId(), providerId) && StringUtils.equals(groupRestrictions.getGroupId(), group) && StringUtils.equals(groupRestrictions.getDivisionId(), division);
        log.info("Returning {} for provider id {}, group {}, division {} for inclusion.", returnValue, providerId, group, division);
        log.info("END : PCPConfigData.matchInclusion");
        return returnValue;
    }

    @MethodExecutionTime
    private boolean matchExclusion(InclusionExclusion inclusionExclusion, String providerId, String group, String division) {
        log.info("START : PCPConfigData.matchExclusion");
        GroupRestrictions groupRestrictions = inclusionExclusion.getGroupRestrictions();
        boolean returnValue = (StringUtils.equals(groupRestrictions.getMasterContractId(), providerId) && StringUtils.equals(groupRestrictions.getGroupId(), group) && StringUtils.equals(groupRestrictions.getDivisionId(), division));
        log.info("Returning {} for provider id {}, group {}, division {} for exclusion.", returnValue, providerId, group, division);
        log.info("END : PCPConfigData.matchExclusion");
        return returnValue;
    }
}
