package com.deltadental.pcp.calculation.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.deltadental.mtv.sync.interservice.MTVSyncServiceClient;
import com.deltadental.mtv.sync.interservice.dto.MemberClaimRequest;
import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;
import com.deltadental.pcp.calculation.enums.STATUS;
import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimsRepo;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import lombok.NoArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@Slf4j
public class PCPValidatorService {

	@Autowired
	@Qualifier("mtvSyncService")
	private MTVSyncServiceClient mtvSyncService;

	@Autowired
	private PCPConfigData pcpConfigData;

	@Autowired
	private ContractMemberClaimsRepo repo;

	@Value("${service.instance.id}")
	private String serviceInstanceId;

	private static final List<String> statusList = new ArrayList<>();

	private static final List<String> statusValidateList = new ArrayList<>();

	static {
		statusList.add(STATUS.RETRY.getStatus());
		statusList.add(STATUS.STAGED.getStatus());
		statusList.add(STATUS.VALIDATED.getStatus());
		statusList.add(STATUS.PCP_ASSIGNED.getStatus());

		statusValidateList.add(STATUS.RETRY.getStatus());
		statusValidateList.add(STATUS.STAGED.getStatus());

	}

	@MethodExecutionTime
	private void validateContractMemberClaim(ContractMemberClaimsEntity contractMemberClaimsEntity) {
		log.info("START PCPValidatorService.validateContractMemberClaim");
		StringBuilder errorBuilder = new StringBuilder();
		String status = STATUS.VALIDATED.getStatus();
		try {
			MemberClaimRequest memberClaimRequest = MemberClaimRequest.builder()
					.memberClaimId(contractMemberClaimsEntity.getClaimId()).build();
			MemberClaimResponse memberClaimResponse = mtvSyncService.memberClaim(memberClaimRequest);

			if (null != memberClaimResponse
					&& (memberClaimResponse.getErrorCode() == null || memberClaimResponse.getErrorMessage() == null)) {
				boolean exclusionFlag = pcpConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(),
						memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber());
				boolean inclusionFlag = pcpConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(),
						memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber());
				if (exclusionFlag || inclusionFlag) {
					List<ServiceLine> serviceLines = memberClaimResponse.getServiceLines();
					if (CollectionUtils.isNotEmpty(serviceLines)) {
						boolean isExplanationCodeValid = pcpConfigData.isExplanationCodeValid(serviceLines);
						boolean isProcedureCodeValid = pcpConfigData.isProcedureCodeValid(serviceLines);
						boolean isClaimStatusValid = pcpConfigData
								.isClaimStatusValid(StringUtils.trimToNull(memberClaimResponse.getClaimStatus()));
						if (isClaimStatusValid && isExplanationCodeValid && isProcedureCodeValid) {
							status = STATUS.VALIDATED.getStatus();
						} else {
							if (!isClaimStatusValid) {
								errorBuilder.append("Claim status").append(memberClaimResponse.getClaimStatus())
										.append("is not valid for PCP assignment!, ");
							}
							if (!isExplanationCodeValid) {
								errorBuilder.append(
										"One of the Service Line Explanation Code[s] is not valid for this claim!,");
							}
							if (!isProcedureCodeValid) {
								errorBuilder.append(
										"One of the Service Line Procedure Code[s] is not valid for this claim!");
							}

							status = STATUS.ERROR.getStatus();
							log.info("PCP Assignment status for claim id {} is {}.",
									contractMemberClaimsEntity.getClaimId(), errorBuilder);
						}
					} else {
						errorBuilder.append("Service Line Items are empty for claim# ")
								.append(contractMemberClaimsEntity.getClaimId());
						status = STATUS.ERROR.getStatus();
					}
				} else {
					if (!exclusionFlag) {
						errorBuilder.append("Provider ").append(memberClaimResponse.getProviderId()).append("Group  ")
								.append(memberClaimResponse.getGroupNumber())
								.append(", Division {} is listed in exlusion list.");
					}
					if (!inclusionFlag) {
						errorBuilder.append("Provider ").append(memberClaimResponse.getProviderId()).append("Group  ")
								.append(memberClaimResponse.getGroupNumber())
								.append(", Division {} is listed in inclusion list.");
					}
					status = STATUS.ERROR.getStatus();
				}
			} else {
				if (memberClaimResponse == null) {
					errorBuilder.append("Claim information not found for claim# ")
							.append(contractMemberClaimsEntity.getClaimId());
					status = STATUS.ERROR.getStatus();
				} else {
					errorBuilder.append(memberClaimResponse.getErrorCode()).append(":")
							.append(memberClaimResponse.getErrorMessage());
					status = STATUS.ERROR.getStatus();
				}
			}
		} catch (Exception e) {
			log.error("Exception occured during retriving member claim information from Metavance Sync Service.", e);
			errorBuilder.append(
					" Exception occured during retriving member claim information from Metavance Sync Service.");
			status = STATUS.RETRY.getStatus();
		}
		String errorMessage = errorBuilder.toString();
		contractMemberClaimsEntity.setErrorMessage(errorMessage);
		contractMemberClaimsEntity.setStatus(status);
		repo.save(contractMemberClaimsEntity);
		log.info("END PCPValidatorService.validateContractMemberClaim");
	}

	@Synchronized
	public void validatePending() {
		log.info("START PCPValidatorService.validatePending()");

		List<ContractMemberClaimsEntity> recordsToValidate = repo.findRecordsToValidate(serviceInstanceId,
				statusValidateList);

		recordsToValidate.forEach(i -> validateContractMemberClaim(i));

		log.info("END PCPValidatorService.validatePending()");
	}
}
