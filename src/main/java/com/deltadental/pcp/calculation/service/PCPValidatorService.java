package com.deltadental.pcp.calculation.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.deltadental.mtv.sync.interservice.MTVSyncServiceClient;
import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ProviderAssignmentRequest;
import com.deltadental.mtv.sync.interservice.dto.ProviderAssignmentResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimServicesEntity;
import com.deltadental.pcp.calculation.entities.MemberProviderEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.error.PCPCalculationServiceErrors;
import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimServicesRepo;
import com.deltadental.pcp.calculation.repos.MemberProviderRepo;
import com.deltadental.pcp.search.interservice.PCPSearchServiceClient;
import com.deltadental.pcp.search.interservice.PCPValidateRequest;
import com.deltadental.pcp.search.interservice.PCPValidateResponse;
import com.deltadental.pcp.search.interservice.pojo.EnrolleeDetail;
import com.deltadental.pcp.search.interservice.pojo.PCPResponse;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.deltadental.platform.common.exception.ServiceException;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@Slf4j
public class PCPValidatorService {

	private static final String PCP_ASSIGN_DCM_701 = "DCM-701";

	private static final String OPERATORID_PCPCALS = "PCPCALS";

	private static final String PCP_ASSIGNMENT_OK = "OK";

	private static final String PCP_VALIDATION_SUCCESS = "Success";

	private static final String PCP_END_DATE_12_31_9999 = "12-31-9999";

	private static final String PCP_STATUS_INITIAL = "INITIAL";

	private static final String REASON_CODE_5NEW = "5NEW";

	private static final String LOOK_A_HEAD_DAYS_90 = "90";

	private static final String DCM_SOURCESYSTEM = "DCM";

	private static final String DC_PRODUCT = "DC";
	
	public static String PCP_VALID_FOR_ENROLLEE = "Input PCP is Valid for the Enrollee"; 
	
	@Autowired
	public MTVSyncServiceClient mtvSyncService;

	@Autowired
	public PCPConfigData pcpConfigData;
	
	@Autowired
	public PCPSearchServiceClient pcpSearchService;
	
	@Autowired
	public MemberProviderRepo memberProviderRepo;
	
	@Autowired
	public MemberClaimServicesRepo memberClaimServicesRepo;
	
	@Autowired
	public MemberClaimRepo memberClaimRepo;
	
	@Autowired
	public ContractMemberClaimRepo repo;
	
	@Value("${service.instance.id}")
	public String serviceInstanceId;

	public ContractMemberClaimEntity contractMemberClaimEntity;
	
	private static final List<Status> SEARCH_STATUS_VALIDATE = List.of(Status.RETRY, Status.STAGED);

	@MethodExecutionTime
	public void validateAndAssignPCP(ContractMemberClaimEntity contractMemberClaimsEntity) {
		log.info("START PCPValidatorService.validateContractMemberClaim");
		StringBuilder errorBuilder = new StringBuilder();
		Status status = Status.STAGED;
		String errorMessage = null;
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
						if (isClaimStatusValid && isExplanationCodeValid && isProcedureCodeValid) {
							try {
								String pcpEffectiveDate = pcpConfigData.calculatePCPEffectiveDate();
								PCPValidateResponse pcpValidateResponse = callPCPValidate(memberClaimResponse, pcpEffectiveDate);
								String pcpValidationMessage = getPCPValidationMessage(pcpValidateResponse);
								log.info("PCP Validation message for claim id {} is {}.", memberClaimResponse.getClaimId(), pcpValidationMessage);
								if (StringUtils.equals(pcpValidateResponse.getProcessStatusCode(), PCP_VALIDATION_SUCCESS)
										&& StringUtils.equals(StringUtils.trimToEmpty(pcpValidationMessage), StringUtils.trimToEmpty(PCP_VALID_FOR_ENROLLEE))) {
									ProviderAssignmentRequest providerAssignmentRequest = buildProviderAssignment(memberClaimResponse, pcpEffectiveDate);
									try {
										ProviderAssignmentResponse providerAssignmentResponse = mtvSyncService.providerAssignment(providerAssignmentRequest);
										if (StringUtils.equals(providerAssignmentResponse.getReturnCode(), PCP_ASSIGNMENT_OK) || StringUtils.equals(providerAssignmentResponse.getErrorCode(), PCP_ASSIGN_DCM_701)) {
											status = Status.PCP_ASSIGNED;
											MemberClaimEntity memberClaimEntity = saveMemberClaimEntity(this.contractMemberClaimEntity, memberClaimResponse);
											saveMemberClaimServices(memberClaimEntity, serviceLines);
											saveMemberProvider(contractMemberClaimEntity.getId(), memberClaimResponse, pcpEffectiveDate, status);
											log.info("PCP Assignment status for claim id {} status is {}.", contractMemberClaimEntity.getClaimId(), status);
										} else {
											status = Status.RETRY;
											errorBuilder.append(providerAssignmentResponse.getErrorCode()).append(" : ").append(providerAssignmentResponse.getErrorMessage());
											log.info("PCP Assignment status for claim id {} status is {} and error message is {}.", contractMemberClaimEntity.getClaimId(), status, errorMessage);
										}										
									} catch (Exception e) {
										log.error("Exception occured during provider assignment from metavance sync Service.", e);
										errorBuilder.append("Exception occured during provider assignment from metavance sync Service.");
										status = Status.RETRY;
									}
								} else {
									errorMessage = pcpValidationMessage;
									status = Status.RETRY;
								}
							} catch (Exception e) {
								log.error("Exception occured during pcp valiation from pcp search Service.", e);
								errorMessage = "Exception occured during pcp valiation from pcp search Service.";
								status = Status.RETRY;
							}	
						}
					}
				} 
			}
		} catch (Exception e) {
			log.error("Exception occured during retriving member claim information from Metavance Sync Service.", e);
			errorBuilder.append(" Exception occured {} "+e.getMessage());
			status = Status.RETRY;
			contractMemberClaimsEntity.incrementRetryCount();
		}
		errorMessage = errorBuilder.toString();
		contractMemberClaimsEntity.setErrorMessage(errorMessage);
		contractMemberClaimsEntity.setStatus(status);
		repo.save(contractMemberClaimsEntity);
		log.info("END PCPValidatorService.validateContractMemberClaim");
	}

	private String getPCPValidationMessage(PCPValidateResponse pcpValidateResponse) {
		String pcpValidationMessage = null;
		if(pcpValidateResponse != null) {
			List<PCPResponse> pcpResponses = pcpValidateResponse.getPcpResponses();
			if(CollectionUtils.isNotEmpty(pcpResponses)) {
				for (PCPResponse pcpResponse : pcpResponses) {
					List<EnrolleeDetail> enrollees = pcpResponse.getEnrollees();
					for (EnrolleeDetail enrolleeDetail : enrollees) {
						List<String> errorMessages = enrolleeDetail.getErrorMessages();
						boolean pcpValidationFlag = errorMessages.stream().anyMatch(error -> StringUtils.equals(StringUtils.trimToEmpty(error), PCP_VALID_FOR_ENROLLEE));
						if(pcpValidationFlag) {
							pcpValidationMessage = StringUtils.trimToEmpty(PCP_VALID_FOR_ENROLLEE);
							break;
						} else {
							pcpValidationMessage = errorMessages.get(0);
						}
					}
				}
			}
		}
		return pcpValidationMessage;
	}
	
	private PCPValidateResponse callPCPValidate(MemberClaimResponse memberClaimResponse, String pcpEffectiveDate) throws ServiceException {
		log.info("START PCPValidatorService.callPCPValidate");	
		PCPValidateRequest pcpValidateRequest = PCPValidateRequest.builder()
				.contractId(memberClaimResponse.getContractId())
				.lookAheadDays(LOOK_A_HEAD_DAYS_90)
				.memberType(memberClaimResponse.getMemberID())
				.mtvPersonId(memberClaimResponse.getPersonId())
				.pcpEffDate(pcpEffectiveDate)
				.product(DC_PRODUCT)
				.pcpEndDate(PCP_END_DATE_12_31_9999)
				.providerId(memberClaimResponse.getProviderId())
				.recordIdentifier(String.valueOf(random()))
				.sourceSystem(DCM_SOURCESYSTEM)
				.build();
		PCPValidateResponse pcpValidateResponse = pcpSearchService.pcpValidate(pcpValidateRequest);
		log.info("END PCPValidatorService.callPCPValidate");	
		return pcpValidateResponse;
	}

	private ProviderAssignmentRequest buildProviderAssignment(MemberClaimResponse memberClaimResponse, String pcpEffectiveDate) {
		ProviderAssignmentRequest providerAssignmentRequest = ProviderAssignmentRequest.builder()
																.contractId(memberClaimResponse.getContractId())
																.enrolleeNumber(memberClaimResponse.getMemberID())
																.pcpEffectiveDate(pcpEffectiveDate)
																.personId(memberClaimResponse.getPersonId())
																.providerContFlag("N")
																.providerId(memberClaimResponse.getProviderId())
																.reasonCode(REASON_CODE_5NEW)
																.sourceSystem(DCM_SOURCESYSTEM)
																.userId(OPERATORID_PCPCALS)
																.build();
		return providerAssignmentRequest;
	}

	private MemberClaimEntity saveMemberClaimEntity(ContractMemberClaimEntity contractMemberClaimsEntity, MemberClaimResponse memberClaimResponse) {		
		MemberClaimEntity memberClaimEntity = MemberClaimEntity.builder()
				.billingProviderId(memberClaimResponse.getBillingProvId())
				.businessLevel4(memberClaimResponse.getBusinessLevel4())
				.businessLevel5(memberClaimResponse.getBusinessLevel5())
				.businessLevel6(memberClaimResponse.getBusinessLevel6())
				.businessLevel7(memberClaimResponse.getBusinessLevel7())
				.claimSource(memberClaimResponse.getClaimSource())
				.claimStatus(memberClaimResponse.getClaimStatus())
				.claimType(memberClaimResponse.getClaimType())
				.groupNumber(memberClaimResponse.getGroupNumber())
				.memberFirstName(memberClaimResponse.getMemberFirstName())
				.memberLastName(memberClaimResponse.getMemberLastName())
				.paidAt(getTimestamp(memberClaimResponse.getPaidTs().getNanos()))
				.personId(memberClaimResponse.getPersonId())
				.receivedAt(getTimestamp(memberClaimResponse.getReceivedTs().getNanos()))
				.resolvedAt(getTimestamp(memberClaimResponse.getResolvedTs().getNanos()))
				.servicesNumber(memberClaimResponse.getServicesNumber())
				.contractMemberClaimsId(contractMemberClaimsEntity.getId())
				.operatorId(OPERATORID_PCPCALS)
				.id(UUID.randomUUID().toString())
				.build();
		memberClaimRepo.save(memberClaimEntity);
		return memberClaimEntity;
	}
	
	private void saveMemberClaimServices(MemberClaimEntity memberClaimEntity, List<ServiceLine> serviceLines) {
		if(!serviceLines.isEmpty()) {
			serviceLines.forEach(serviceLine -> {
				MemberClaimServicesEntity memberClaimServicesEntity = MemberClaimServicesEntity.builder()
						.claimType(serviceLine.getClaimType())
						.encounterFlag(serviceLine.getEncounterFlag())
						.explnCode(serviceLine.getExplnCode())
						.procedureCode(serviceLine.getProcedureCode())
						.sequenceNumber(serviceLine.getSequenceNumber())
						.serviceNumber(serviceLine.getServiceNumber())
						.servicePaidAt(getTimestamp(serviceLine.getServicePaidTs().getNanos()))
						.serviceResolutionAt(getTimestamp(serviceLine.getServiceResolutionTs().getNanos()))
						.memberClaimId(memberClaimEntity.getId())
						.operatorId(OPERATORID_PCPCALS)
						.id(UUID.randomUUID().toString())
						.build();
				memberClaimServicesRepo.save(memberClaimServicesEntity);
			});
		}
	}
	
	private MemberProviderEntity saveMemberProvider(String id, MemberClaimResponse memberClaimResponse, String pcpEffectiveDate, Status status) {
		MemberProviderEntity memberProviderEntity = MemberProviderEntity.builder()
				.id(UUID.randomUUID().toString())
				.claimStatus(memberClaimResponse.getClaimStatus())
				.personId(memberClaimResponse.getPersonId())
				.memberId(memberClaimResponse.getMemberID())
				.pcpEffectiveDate(pcpEffectiveDate)
				.reasonCode(REASON_CODE_5NEW)
				.sourceSystem(DCM_SOURCESYSTEM)
				.status(PCP_STATUS_INITIAL)
				.operatorId(OPERATORID_PCPCALS)
				.contractId(memberClaimResponse.getContractId())
				.contractMemberClaimId(id) //Fixme:Verify
				.status(status.name())
				.providerId(memberClaimResponse.getProviderId())
//				.businessLevelAssnId(memberClaimResponse.getb)
//				.practiceLocationId(memberClaimResponse.getpr)
//				.providerContractId(memberClaimResponse.getpr)
				.build();
		memberProviderRepo.save(memberProviderEntity);
		return memberProviderEntity;
	}
	
	private Timestamp getTimestamp(int nanos) {
		Timestamp ts=new Timestamp(nanos);  
		return ts;
	}
	
	private int random() {
		try {
			Random rand = SecureRandom.getInstanceStrong();
			return rand.nextInt(10) + 1;
		} catch (NoSuchAlgorithmException e) {
			return 0;
		}		
	}
	
	public void validatePending() {
		log.info("START PCPValidatorService.validatePending()");

		List<ContractMemberClaimEntity> recordsToValidate = repo.findByInstanceIdWhereStatusInList(serviceInstanceId, SEARCH_STATUS_VALIDATE);

		recordsToValidate.forEach(i -> validateAndAssignPCP(i));

		log.info("END PCPValidatorService.validatePending()");
	}
}
