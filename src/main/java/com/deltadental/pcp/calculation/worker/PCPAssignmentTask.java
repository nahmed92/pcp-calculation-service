package com.deltadental.pcp.calculation.worker;

import com.deltadental.mtv.sync.interservice.MTVSyncServiceClient;
import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimServicesRepo;
import com.deltadental.pcp.calculation.repos.MemberProviderRepo;
import com.deltadental.pcp.calculation.service.PCPAssignmentService;
import com.deltadental.pcp.search.interservice.PCPSearchServiceClient;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Component
@Scope("prototype")
@Data
@Slf4j
public class PCPAssignmentTask implements Runnable {

    @Autowired
    private PCPSearchServiceClient pcpSearchService;

    @Autowired
    private MTVSyncServiceClient mtvSyncService;

    @Autowired
    private PCPConfigData pcpConfigData;

    @Autowired
    private MemberProviderRepo memberProviderRepo;

    @Autowired
    private MemberClaimServicesRepo memberClaimServicesRepo;

    @Autowired
    private MemberClaimRepo memberClaimRepo;

    @Autowired
    private ContractMemberClaimRepo contractMemberClaimRepo;

    @Autowired
    private PCPAssignmentService pcpAssignmentService;

    private ContractMemberClaimEntity contractMemberClaimEntity;

	@MethodExecutionTime
	public void validateAndAssignProvider() {
		log.info("START PCPCalculationService.processPCPAssignment.");
		log.info("Processing {} ", contractMemberClaimEntity);
		StringBuilder errorMessageBuilder = new StringBuilder();
		List<String> memberClaimResponseList = List.of(contractMemberClaimEntity.getClaimId());
		try {
			List<MemberClaimResponse> memberClaimsResponse = mtvSyncService.memberClaim(memberClaimResponseList);
			if(CollectionUtils.isNotEmpty(memberClaimsResponse)) {
			MemberClaimResponse memberClaimResponse = memberClaimsResponse.get(0);
			if (null != memberClaimResponse && (StringUtils.isBlank(memberClaimResponse.getErrorCode()) || StringUtils.isBlank(memberClaimResponse.getErrorMessage()))) {
				boolean exclusionFlag = pcpConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber());
				boolean inclusionFlag = pcpConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber());
				if (exclusionFlag || inclusionFlag) {
					List<ServiceLine> serviceLines = memberClaimResponse.getServiceLines();
					if (CollectionUtils.isNotEmpty(serviceLines)) {
						boolean isExplanationCodeValid = pcpConfigData.isExplanationCodeValid(serviceLines);
						boolean isProcedureCodeValid = pcpConfigData.isProcedureCodeValid(serviceLines);
						boolean isClaimStatusValid = pcpConfigData.isClaimStatusValid(StringUtils.trimToNull(memberClaimResponse.getClaimStatus()));
						log.info("Claim id {} , isClaimStatusValid {}, isProcedureCodeValid {} and isExplanationCodeValid {} ",memberClaimResponse.getClaimId(),isClaimStatusValid,isProcedureCodeValid,isExplanationCodeValid);
						if (isClaimStatusValid && isExplanationCodeValid && isProcedureCodeValid) {
							pcpAssignmentService.process(contractMemberClaimEntity, memberClaimResponse);
						} else {
							if (!isClaimStatusValid) {
								appendColon(errorMessageBuilder);
								errorMessageBuilder.append(String.format("Claim status %s is not valid for PCP assignment!", StringUtils.trimToNull(memberClaimResponse.getClaimStatus())));
							}
							if (!isExplanationCodeValid) {
								appendColon(errorMessageBuilder);
								errorMessageBuilder.append("One of the Service Line Explanation Code[s] is not valid for this claim.");
							}
							if (!isProcedureCodeValid) {
								appendColon(errorMessageBuilder);
								errorMessageBuilder.append(", One of the Service Line Procedure Code[s] is not valid for this claim.");
							}
							log.info("PCP Assignment status for claim id {} is {}.", contractMemberClaimEntity.getClaimId(), errorMessageBuilder);
							contractMemberClaimEntity.setStatus(Status.RETRY);
							contractMemberClaimEntity.setErrorMessage(errorMessageBuilder.toString());
						}
					} else {
						errorMessageBuilder.append(String.format("Service Line Items are empty for claim# %s ", contractMemberClaimEntity.getClaimId()));
						contractMemberClaimEntity.setStatus(Status.RETRY);
						contractMemberClaimEntity.setErrorMessage(errorMessageBuilder.toString());
					}
				} else {
					if (!exclusionFlag) {
						errorMessageBuilder.append(String.format("Provider %s, Group %s, Division %s is listed in exclusion list.", memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()));
						contractMemberClaimEntity.setStatus(Status.PCP_EXCLUDED);
					}
					if (!inclusionFlag) {
						appendColon(errorMessageBuilder);
						errorMessageBuilder.append(String.format("Provider %s, Group %s, Division %s is not listed in inclusion list.", memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()));
						contractMemberClaimEntity.setStatus(Status.PCP_NOT_INCLUDED);
					}
					contractMemberClaimEntity.setErrorMessage(errorMessageBuilder.toString());
				}
			} else {
				if (memberClaimResponse == null) {
					errorMessageBuilder.append(String.format("Claim information not found for claim # %s", contractMemberClaimEntity.getClaimId()));
					log.info("Marking as {} for Claim id {} with member claim response is null ",Status.CLAIM_NOT_FOUND,contractMemberClaimEntity.getClaimId());
					contractMemberClaimEntity.setStatus(Status.CLAIM_NOT_FOUND);
					contractMemberClaimEntity.setErrorMessage(errorMessageBuilder.toString());
				} else {
					errorMessageBuilder.append(String.join(" : ", memberClaimResponse.getErrorCode(), memberClaimResponse.getErrorMessage()));
					contractMemberClaimEntity.setStatus(Status.FAILED);
					log.info("Marking as {} for Claim id {} with member claim response {} ",Status.FAILED,contractMemberClaimEntity.getClaimId(),  errorMessageBuilder);
					contractMemberClaimEntity.setErrorMessage(errorMessageBuilder.toString());
				}
			}
			}
		} catch (Exception e) {
			log.error("Exception occured during retriving member claim information from Metavance Sync Service.", e);
			errorMessageBuilder.append("Exception occured during retriving member claim information from Metavance Sync Service.");
			contractMemberClaimEntity.setStatus(Status.RETRY);
			contractMemberClaimEntity.setErrorMessage(errorMessageBuilder.toString());
		}		
		contractMemberClaimRepo.save(contractMemberClaimEntity);
		log.info("END PCPCalculationService.processPCPAssignment");
	}
	
    private void appendColon(StringBuilder strBuilder) {
        if (strBuilder != null && strBuilder.length() > 0) {
            strBuilder.append(": ");
        }
    }

    @Override
    public void run() {
        validateAndAssignProvider();
    }
}
