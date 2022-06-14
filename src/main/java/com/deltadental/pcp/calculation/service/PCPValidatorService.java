package com.deltadental.pcp.calculation.service;

import com.deltadental.mtv.sync.interservice.MTVSyncServiceClient;
import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
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

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
	private PCPAssignmentService pcpAssignmentService;

	@Value("${service.instance.id}")
	private String serviceInstanceId;

	SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");

	private static final List<Status> SEARCH_STATUS_VALIDATE = List.of(Status.RETRY, Status.STAGED);

	@MethodExecutionTime
	public void validateAndAssignPCP(List<ContractMemberClaimEntity> contractMemberClaimsEntities) {
		log.info("START PCPValidatorService.validateContractMemberClaim");
		Multimap<String, MemberClaimResponse> memberWiseResponseMultiMap = ArrayListMultimap.create();

		try {
			List<MemberClaimResponse> memberClaimsResponse = mtvSyncService.memberClaim(getClaimIds(contractMemberClaimsEntities));
			if (CollectionUtils.isNotEmpty(memberClaimsResponse)) {
				memberClaimsResponse.stream().forEach(memberClaimResponse -> {
					if (null != memberClaimResponse && (StringUtils.isBlank(memberClaimResponse.getErrorCode())
							|| StringUtils.isBlank(memberClaimResponse.getErrorMessage()))) {
						boolean exclusionFlag = pcpConfigData.isProviderInExclusionList(
								memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(),
								memberClaimResponse.getDivisionNumber());
						boolean inclusionFlag = pcpConfigData.isProviderInInclusionList(
								memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(),
								memberClaimResponse.getDivisionNumber());
						// TODO : Get more info on when to validate inclusion and exclusion list
						if (exclusionFlag || inclusionFlag) {
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
						}
					}
				});
			}

		} catch (Exception e) {
			log.error("Exception occurred during retrieving member claim information from Metavance Sync Service.", e);
			contractMemberClaimsEntities.stream().forEach(entity -> {
				entity.setErrorMessage(
						"Exception occurred during retrieving member claim information from Metavance Sync Service. "
								+ e.getMessage());
				entity.setStatus(Status.RETRY);
			});
		}
		if(!memberWiseResponseMultiMap.isEmpty()) {
		repo.saveAll(contractMemberClaimsEntities);
		pcpAssignmentService(contractMemberClaimsEntities, memberWiseResponseMultiMap);
		}
		log.info("END PCPValidatorService.validateContractMemberClaim");
	}

	private void pcpAssignmentService(List<ContractMemberClaimEntity> contractMemberClaimEntities,
			Multimap<String, MemberClaimResponse> memberWiseResponseMap) {
 		    contractMemberClaimEntities.forEach(contractMemberClaim -> {
			List<MemberClaimResponse> members = (List<MemberClaimResponse>) memberWiseResponseMap.get(contractMemberClaim.getMemberId());
			MemberClaimResponse memberClaimResponse = calculateLatestClaim(members);
			pcpAssignmentService.process(contractMemberClaim, memberClaimResponse);
		});
	}

	public MemberClaimResponse calculateLatestClaim(List<MemberClaimResponse> members) {
		if(members.size()==1) {
			return members.get(0);
		}
		for (MemberClaimResponse memberClaim : members) {
			Date maxFromDate = memberClaim.getServiceLines().stream().map(u -> u.getFromDate()).max(Date::compareTo)
					.get();
			Date maxThruDate = memberClaim.getServiceLines().stream().map(u -> u.getThruDate()).max(Date::compareTo)
					.get();
			memberClaim.setFromDate(maxFromDate);
			memberClaim.setThruDate(maxThruDate);
		}
		
		MemberClaimResponse memberClaimResponse = null;
		Optional<MemberClaimResponse> collectData = members.stream().collect(Collectors.maxBy(Comparator
				.comparing(MemberClaimResponse::getFromDate).thenComparing(MemberClaimResponse::getThruDate)
				.thenComparing(MemberClaimResponse::getReceivedTs)));
		if (collectData.isPresent()) {
			memberClaimResponse = collectData.get();
		}

		return memberClaimResponse;
	}

	private List<String> getClaimIds(List<ContractMemberClaimEntity> contractMemberClaimEntities) {
		return contractMemberClaimEntities.stream().map(i -> i.getClaimId()).collect(Collectors.toList());
	}

	public void validatePending() {
		log.info("START PCPValidatorService.validatePending()");

		List<ContractMemberClaimEntity> recordsToValidate = repo.findByInstanceIdWhereStatusInList(serviceInstanceId,
				SEARCH_STATUS_VALIDATE);
		validateAndAssignPCP(recordsToValidate);
		log.info("END PCPValidatorService.validatePending()");
	}
}
