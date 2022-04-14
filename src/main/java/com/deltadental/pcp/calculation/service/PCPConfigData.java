package com.deltadental.pcp.calculation.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.deltadental.mtv.sync.service.ServiceLine;
import com.deltadental.pcp.config.service.PCPConfigService;
import com.deltadental.pcp.config.service.PcpConfigResponse;
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
	private PCPConfigService pcpConfigService;

	private String lookAHeadDays;
	private List<PcpConfigResponse> claimStatusList = new ArrayList<>();
	private List<PcpConfigResponse> procedureCodes = new ArrayList<>();
	private List<PcpConfigResponse> explanationCodes = new ArrayList<>();

	@Override
    public void afterPropertiesSet() throws Exception {
		claimStatusList.clear();
		procedureCodes.clear();
		explanationCodes.clear();
		log.info("Cleared all the pcp config data!");
		claimStatuses();
		log.info("Claim status list size : "+claimStatusList.size());
		explanationCodes();
		log.info("Explanation codes list size : "+explanationCodes.size());
		procedureCodes();
		log.info("Procedure codes list size : "+procedureCodes.size());
	}
	
	@Scheduled(cron = "${pcp.config.corn.job.scheduler}", zone = "${pcp.calculation.job.scheduler.zone}")
	@Synchronized
	public void refreshPCPConfigData() {
		claimStatusList.clear();
		procedureCodes.clear();
		explanationCodes.clear();
		log.info("Cleared all the pcp config data!");
		claimStatuses();
		log.info("Refreshed Claim status list size : "+claimStatusList.size());
		explanationCodes();
		log.info("Refreshed Explanation codes list size : "+explanationCodes.size());
		procedureCodes();
		log.info("Refreshed Procedure codes list size : "+procedureCodes.size());
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
		String jsonClaimStatusStr = pcpConfigService.claimStatus();
		List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonClaimStatusStr);
		setClaimStatusList(pcpConfigResponses);
	}

	private void explanationCodes() {
		String jsonExplanationCodeStr = pcpConfigService.explanationCode();
		List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonExplanationCodeStr);
		setExplanationCodes(pcpConfigResponses);
	}

	private void procedureCodes() {
		String jsonProcedureCodeStr = pcpConfigService.procedureCode();
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
			List<PcpConfigResponse> pcpConfigResponses = this.getExplanationCodes();
			if (serviceLines.size() == 1) {
				return pcpConfigResponses.stream().anyMatch(pcpConfigResponse -> StringUtils.equals(pcpConfigResponse.getCodeValue(), serviceLines.get(0).getExplnCode()));
			} else {
				for(ServiceLine serviceLine : serviceLines) {
					for (PcpConfigResponse pcpConfigResponse : pcpConfigResponses) {
						if(StringUtils.equals(StringUtils.trim(pcpConfigResponse.getCodeValue()), StringUtils.trim(serviceLine.getExplnCode()))) {
							isExplanationCodeValid = true;
							break;
						}
					}
					if(isExplanationCodeValid) {
						break;
					}
				}
			}
		}
		return isExplanationCodeValid;
	}
	
	public boolean isProcedureCodeValid(List<ServiceLine> serviceLines) {
		boolean isProcedureCodeValid = true;
		if (serviceLines != null && !serviceLines.isEmpty()) {
			List<PcpConfigResponse> pcpConfigResponses = this.getProcedureCodes();
			if (serviceLines.size() == 1) {
				return pcpConfigResponses.stream().noneMatch(pcpConfigResponse -> StringUtils.equals(pcpConfigResponse.getCodeValue(), serviceLines.get(0).getProcedureCode()));
			} else {
				for (ServiceLine serviceLine : serviceLines) {
					for (PcpConfigResponse pcpConfigResponse : pcpConfigResponses) {
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
		}
		return isProcedureCodeValid;
	}
}
