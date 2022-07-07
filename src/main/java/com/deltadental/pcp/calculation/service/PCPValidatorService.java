package com.deltadental.pcp.calculation.service;

import com.deltadental.mtv.sync.interservice.MTVSyncServiceClient;
import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import com.deltadental.pcp.calculation.util.MemberClaimUtils;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	private MemberClaimUtils memberClaimUtils;

	@Autowired
	private PCPAssignmentService pcpAssignmentService;

	@Value("${service.instance.id}")
	private String serviceInstanceId;
	private static final List<Status> SEARCH_STATUS_VALIDATE = List.of(Status.RETRY, Status.STAGED, Status.VALIDATED, Status.PCP_EXCLUDED, Status.PCP_NOT_INCLUDED);

	@MethodExecutionTime
	public void validateAndAssignPCP(List<ContractMemberClaimEntity> contractMemberClaimsEntities) {
		log.info("START PCPValidatorService.validateContractMemberClaim");
		Multimap<String, MemberClaimResponse> memberWiseResponseMultiMap = ArrayListMultimap.create();
		try {
			List<MemberClaimResponse> memberClaimsResponses = mtvSyncService.memberClaim(memberClaimUtils.getClaimIds(contractMemberClaimsEntities));
			if (CollectionUtils.isNotEmpty(memberClaimsResponses)) {
				for (MemberClaimResponse memberClaimResponse:memberClaimsResponses) {
					if (null != memberClaimResponse && (StringUtils.isBlank(memberClaimResponse.getErrorCode())
							|| StringUtils.isBlank(memberClaimResponse.getErrorMessage()))) {
						boolean exclusionFlag = pcpConfigData.isProviderInExclusionList(
								memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(),
								memberClaimResponse.getDivisionNumber());
						if(exclusionFlag) {
							log.info("Provider {} excluded, not assigning for claim id {}", memberClaimResponse.getProviderId(), memberClaimResponse.getClaimId());
							continue;
						}
						boolean inclusionFlag = pcpConfigData.isProviderInInclusionList(
								memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(),
								memberClaimResponse.getDivisionNumber());
						if (inclusionFlag) {
							List<ServiceLine> serviceLines = memberClaimResponse.getServiceLines();
							if (CollectionUtils.isNotEmpty(serviceLines)) {
								boolean isClaimStatusValid = pcpConfigData.isClaimStatusValid(
										StringUtils.trimToNull(memberClaimResponse.getClaimStatus()));
								boolean isExplanationCodeValid = pcpConfigData.isExplanationCodeValid(serviceLines);
								boolean isProcedureCodeValid = pcpConfigData.isProcedureCodeValid(serviceLines);
								log.info(
										"Claim id {} , isClaimStatusValid {}, isProcedureCodeValid {} and isExplanationCodeValid {} ",
										memberClaimResponse.getClaimId(), isClaimStatusValid, isProcedureCodeValid,
										isExplanationCodeValid);
								if (isClaimStatusValid && isExplanationCodeValid && isProcedureCodeValid) {
									memberWiseResponseMultiMap.put(memberClaimResponse.getMemberID(), memberClaimResponse);
								}
							}
						} else {
							log.info("Provider {} not included, not assigning for claim id {}", memberClaimResponse.getProviderId(), memberClaimResponse.getClaimId());
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Exception occurred during retrieving member claim information from Metavance Sync Service.", e);
			contractMemberClaimsEntities.forEach(entity -> {
				entity.setErrorMessage(
						"Exception occurred during retrieving member claim information from Metavance Sync Service. "
								+ e.getMessage());
				entity.setStatus(Status.RETRY);
			});
		}
		if(!memberWiseResponseMultiMap.isEmpty()) {
			pcpAssignmentService(contractMemberClaimsEntities, memberWiseResponseMultiMap);
			repo.saveAll(contractMemberClaimsEntities);
		}
		log.info("END PCPValidatorService.validateContractMemberClaim");
	}

	@MethodExecutionTime
	private void pcpAssignmentService(List<ContractMemberClaimEntity> contractMemberClaimEntities,
			Multimap<String, MemberClaimResponse> memberWiseResponseMap) {
 		    contractMemberClaimEntities.forEach(contractMemberClaimEntity -> {
			List<MemberClaimResponse> members = (List<MemberClaimResponse>) memberWiseResponseMap.get(contractMemberClaimEntity.getMemberId());
			MemberClaimResponse memberClaimResponse = memberClaimUtils.calculateLatestClaim(members);
			pcpAssignmentService.process(contractMemberClaimEntity, memberClaimResponse);
		});
	}

	@MethodExecutionTime
	@Transactional
	public void validatePending() {
		log.info("START PCPValidatorService.validatePending()");
		List<ContractMemberClaimEntity> recordsToValidate = repo.findByInstanceIdWhereStatusInList(serviceInstanceId,
				SEARCH_STATUS_VALIDATE);
			if(CollectionUtils.isNotEmpty(recordsToValidate)) {
				Map<String, List<ContractMemberClaimEntity>> contractMemberClaimEntityMap = recordsToValidate.stream().collect
				        (Collectors.groupingBy(ContractMemberClaimEntity::getContractId));
				contractMemberClaimEntityMap.values().forEach(value -> {
				validateAndAssignPCP(value);
			});
	    }
		log.info("END PCPValidatorService.validatePending()");
	}
}
