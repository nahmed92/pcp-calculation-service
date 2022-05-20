package com.deltadental.pcp.calculation.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.deltadental.mtv.sync.interservice.MTVSyncServiceClient;
import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@Slf4j
public class PCPValidatorService {

	@Autowired
	private MTVSyncServiceClient mtvSyncService;

	@Autowired
	private PCPConfigData pcpConfigData;
	
	@Autowired
	private ContractMemberClaimRepo repo;

	@Autowired
	private PCPAssignmentService pcpAssignmentService;
	
	@Value("${service.instance.id}")
	private String serviceInstanceId;
	
	private static final List<Status> SEARCH_STATUS_VALIDATE = List.of(Status.RETRY, Status.STAGED);

	@MethodExecutionTime
	public void validateAndAssignPCP(ContractMemberClaimEntity contractMemberClaimsEntity) {
		log.info("START PCPValidatorService.validateContractMemberClaim");
		try {
			MemberClaimResponse memberClaimResponse = mtvSyncService.memberClaim(contractMemberClaimsEntity.getClaimId());
			if (null != memberClaimResponse
					&& (StringUtils.isBlank(memberClaimResponse.getErrorCode()) || StringUtils.isBlank(memberClaimResponse.getErrorMessage()))) {
				boolean exclusionFlag = pcpConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber());
				boolean inclusionFlag = pcpConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber());
				// TODO : Get more info on when to validate inclusion and exclusion list
				if (exclusionFlag || inclusionFlag) {
					List<ServiceLine> serviceLines = memberClaimResponse.getServiceLines();
					if (CollectionUtils.isNotEmpty(serviceLines)) {
						boolean isClaimStatusValid = pcpConfigData.isClaimStatusValid(StringUtils.trimToNull(memberClaimResponse.getClaimStatus()));
						boolean isExplanationCodeValid = pcpConfigData.isExplanationCodeValid(serviceLines);
						boolean isProcedureCodeValid = pcpConfigData.isProcedureCodeValid(serviceLines);
						log.info("Claim id {} , isClaimStatusValid {}, isProcedureCodeValid {} and isExplanationCodeValid {} ",memberClaimResponse.getClaimId(),isClaimStatusValid,isProcedureCodeValid,isExplanationCodeValid);
						if (isClaimStatusValid && isExplanationCodeValid && isProcedureCodeValid) {
							pcpAssignmentService.process(contractMemberClaimsEntity, memberClaimResponse);
						}
					}
				} 
			}
		} catch (Exception e) {
			log.error("Exception occured during retriving member claim information from Metavance Sync Service.", e);
			contractMemberClaimsEntity.incrementRetryCount();
			contractMemberClaimsEntity.setErrorMessage("Exception occured during retriving member claim information from Metavance Sync Service. "+e.getMessage());
			contractMemberClaimsEntity.setStatus(Status.RETRY);
		}
		repo.save(contractMemberClaimsEntity);
		log.info("END PCPValidatorService.validateContractMemberClaim");
	}

	public void validatePending() {
		log.info("START PCPValidatorService.validatePending()");

		List<ContractMemberClaimEntity> recordsToValidate = repo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE);

		recordsToValidate.forEach(i -> validateAndAssignPCP(i));

		log.info("END PCPValidatorService.validatePending()");
	}
}
