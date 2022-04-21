package com.deltadental.pcp.calculation.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.deltadental.mtv.sync.service.MTVSyncService;
import com.deltadental.mtv.sync.service.MemberClaimRequest;
import com.deltadental.mtv.sync.service.MemberClaimResponse;
import com.deltadental.mtv.sync.service.ServiceLine;
import com.deltadental.pcp.calculation.controller.STATUS;
import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimsRepo;
import com.deltadental.pcp.calculation.scheduler.PCPCalculationServiceScheduler;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class PCPCalculationService {
	
	@Autowired
	@Qualifier("mtvSyncService")
	private MTVSyncService mtvSyncService;
	
	@Autowired
	private PCPConfigData pcpConfigData;

	@Autowired
	private ContractMemberClaimsRepo contractMemberClaimsRepo; 
	
	@Autowired
	private PCPCalculationServiceScheduler pcpCalculationServiceScheduler; 
	
	@Value("${service.instance.id}")
	private String serviceInstanceId;

	private static final List<String> statusList = new ArrayList<>();
	
	static {
		statusList.add(STATUS.RETRY.getStatus());
		statusList.add(STATUS.STAGED.getStatus());
		statusList.add(STATUS.VALIDATED.getStatus());
		statusList.add(STATUS.PCP_ASSIGNED.getStatus());
	}
	
	public void assignPCPsToMembers() {
		log.info("START PCPCalculationService.assignPCPsToMembers");
		pcpCalculationServiceScheduler.processPendingPCPAssignmentRequest();
		log.info("END PCPCalculationService.assignPCPsToMembers");
	}
	
	public void stageMemberContractClaimRecord(MemberContractClaimRequest validateProviderRequest) {
		log.info("START PCPCalculationService.assignMemberPCP");
		List<ContractMemberClaimsEntity> memberClaimsEntities = contractMemberClaimsRepo.findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatusInList(
				StringUtils.trimToNull(validateProviderRequest.getClaimId()), 
				StringUtils.trimToNull(validateProviderRequest.getContractId()), 
				StringUtils.trimToNull(validateProviderRequest.getMemberId()), 
				StringUtils.trimToNull(validateProviderRequest.getProviderId()),
				StringUtils.trimToNull(validateProviderRequest.getState()),
				statusList);
		if(CollectionUtils.isEmpty(memberClaimsEntities)) {
			saveAndValidateContractMemberClaims(validateProviderRequest);
			log.info("Record inserted in contract member claims table : "+validateProviderRequest.toString());
		} else {
			log.info("Record already exists in contract member claims table : "+validateProviderRequest.toString());
		}
		log.info("END PCPCalculationService.assignMemberPCP");
	}

	public void stageMemberContractClaimRecords(List<MemberContractClaimRequest> validateProviderRequests) {
		log.info("START PCPCalculationService.assignMemberPCP");
		if(null != validateProviderRequests && !validateProviderRequests.isEmpty()) {
			validateProviderRequests.forEach(validateProviderRequest -> saveAndValidateContractMemberClaims(validateProviderRequest));
		}
		log.info("END PCPCalculationService.assignMemberPCP");
	}
	
	private void saveAndValidateContractMemberClaims(MemberContractClaimRequest validateProviderRequest) {
		log.info("START PCPCalculationService.saveContractMemberClaims");
		ContractMemberClaimsEntity contractMemberClaimsEntity = ContractMemberClaimsEntity.builder()
				.claimId(StringUtils.trimToNull(validateProviderRequest.getClaimId()))
				.contractId(StringUtils.trimToNull(validateProviderRequest.getContractId()))
				.memberId(StringUtils.trimToNull(validateProviderRequest.getMemberId()))
				.providerId(StringUtils.trimToNull(validateProviderRequest.getProviderId()))
				.state(StringUtils.trimToNull(validateProviderRequest.getState()))
				.operatorId(StringUtils.trimToNull(validateProviderRequest.getOperatorId()))
				.instanceId(StringUtils.trimToNull(serviceInstanceId))
				.status(STATUS.STAGED.name())
				.build();
		contractMemberClaimsRepo.save(contractMemberClaimsEntity);
		validateContractMemberClaim(contractMemberClaimsEntity);
		contractMemberClaimsRepo.save(contractMemberClaimsEntity);
		log.info("START PCPCalculationService.saveContractMemberClaims");
	}
	
	private void validateContractMemberClaim(ContractMemberClaimsEntity contractMemberClaimsEntity) {
		log.info("START PCPCalculationService.validateContractMemberClaim");
		String errorMessage = null;
		String status = STATUS.VALIDATED.getStatus();
		try {
			MemberClaimRequest memberClaimRequest = MemberClaimRequest.builder().memberClaimId(contractMemberClaimsEntity.getClaimId()).build();
			MemberClaimResponse memberClaimResponse = mtvSyncService.memberClaim(memberClaimRequest);
			
			if (null != memberClaimResponse && (memberClaimResponse.getErrorCode() == null || memberClaimResponse.getErrorMessage() == null)) {
				// TODO : Find division number
				boolean exclusionFlag = pcpConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), null);
				boolean inclusionFlag = pcpConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), null);
				if(exclusionFlag || inclusionFlag) {
					List<ServiceLine> serviceLines = memberClaimResponse.getServiceLines();
					if(CollectionUtils.isNotEmpty(serviceLines)) {
						boolean isExplanationCodeValid = pcpConfigData.isExplanationCodeValid(serviceLines);
						boolean isProcedureCodeValid = pcpConfigData.isProcedureCodeValid(serviceLines);
						boolean isClaimStatusValid = pcpConfigData.isClaimStatusValid(StringUtils.trimToNull(memberClaimResponse.getClaimStatus()));	
						if (isClaimStatusValid && isExplanationCodeValid && isProcedureCodeValid) {
							status = STATUS.VALIDATED.getStatus();		
						} else {
							if (!isClaimStatusValid) {
								errorMessage = String.format("Claim status %s is not valid for PCP assignment!", StringUtils.trimToNull(memberClaimResponse.getClaimStatus()));
							}
							if (!isExplanationCodeValid) {
								if (StringUtils.isNotBlank(errorMessage)) {
									errorMessage = String.join(", ", errorMessage, "One of the Service Line Explanation Code[s] is not valid for this claim!");
								} else {
									errorMessage = "One of the Service Line Explanation Code[s] is not valid for this claim!";
								}
							}	
							if (!isProcedureCodeValid) {
								if (StringUtils.isNotBlank(errorMessage)) {
									errorMessage = String.join(", ", errorMessage, "One of the Service Line Procedure Code[s] is not valid for this claim!");
								} else {
									errorMessage = "One of the Service Line Procedure Code[s] is not valid for this claim!";
								}
							}
							log.info(errorMessage);
							status = STATUS.ERROR.getStatus();
							log.info("PCP Assignment status for claim id {} is {}.", contractMemberClaimsEntity.getClaimId() , errorMessage);
						}
					} else {
						errorMessage = String.format("Service Line Items are empty for claim# %s ",contractMemberClaimsEntity.getClaimId());
						status = STATUS.ERROR.getStatus();
					}
				} else {
					if(!exclusionFlag) {
						errorMessage = String.format("Provider {}, Group {}, Division {} is listed in exlusion list.", memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), "");
					}
					if(!inclusionFlag) {
						if (StringUtils.isNotBlank(errorMessage)) {
							errorMessage = String.join(", ", errorMessage, String.format("Provider {}, Group {}, Division {} is listed in inclusion list.", memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), ""));
						}
					}
					status = STATUS.ERROR.getStatus();	
				}
			}  else {
				if(memberClaimResponse == null) {
					errorMessage = String.format("Claim information not found for claim# ", contractMemberClaimsEntity.getClaimId());
					status = STATUS.ERROR.getStatus();	
				} else {
					errorMessage = String.join(" : ", memberClaimResponse.getErrorCode(), memberClaimResponse.getErrorMessage());
					status = STATUS.ERROR.getStatus();
				}						
			}
		} catch (Exception e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			log.error("Exception occured during retriving member claim information from Metavance Sync Service.", stacktrace);
			errorMessage = "Exception occured during retriving member claim information from Metavance Sync Service.";
			status = STATUS.RETRY.getStatus();
		}
		contractMemberClaimsEntity.setErrorMessage(errorMessage);
		contractMemberClaimsEntity.setStatus(status);
		log.info("END PCPCalculationService.validateContractMemberClaim");
	}
}
