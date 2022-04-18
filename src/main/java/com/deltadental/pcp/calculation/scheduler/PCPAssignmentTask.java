package com.deltadental.pcp.calculation.scheduler;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.deltadental.mtv.sync.service.MTVSyncService;
import com.deltadental.mtv.sync.service.MemberClaimRequest;
import com.deltadental.mtv.sync.service.MemberClaimResponse;
import com.deltadental.mtv.sync.service.ProviderAssignmentRequest;
import com.deltadental.mtv.sync.service.ProviderAssignmentResponse;
import com.deltadental.mtv.sync.service.ServiceLine;
import com.deltadental.pcp.calculation.controller.STATUS;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimServicesEntity;
import com.deltadental.pcp.calculation.entities.MemberProviderEntity;
import com.deltadental.pcp.calculation.error.PCPCalculationServiceErrors;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimsRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimServicesRepo;
import com.deltadental.pcp.calculation.repos.MemberProviderRepo;
import com.deltadental.pcp.calculation.service.PCPConfigData;
import com.deltadental.pcp.search.service.PCPSearchService;
import com.deltadental.pcp.search.service.PCPValidateResponse;
import com.deltadental.pcp.search.service.PcpValidateRequest;
import com.deltadental.pcp.search.service.pojos.EnrolleeDetail;
import com.deltadental.pcp.search.service.pojos.PCPResponse;
import com.deltadental.platform.common.exception.ServiceException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Component
@Scope("prototype")
@Data
@Slf4j
public class PCPAssignmentTask implements Runnable {

	private static final String OPERATORID_PCPCALS = "PCPCALS";

	private static final String PCP_ASSIGNMENT_OK = "OK";

	private static final String PCP_VALIDATION_SUCCESS = "Success";

	private static final String PCP_END_DATE_12_31_9999 = "12-31-9999";

	private static final String PCP_STATUS_INITIAL = "INITIAL";

	private static final String REASON_CODE_5NEW = "5NEW";

	private static final String LOOK_A_HEAD_DAYS_90 = "90";

	private static final String DCM_SOURCESYSTEM = "DCM";

	private static final String DC_PRODUCT = "DC";
	
	public static String PCP_VALID_FOR_ENROLLEE = " Input PCP is Valid for the Enrollee "; 
	
	@Autowired
	private PCPSearchService pcpSearchService;
	
	@Autowired
	private MTVSyncService mtvSyncService;
	
	@Autowired
	private PCPConfigData pcpConfigData;

    @Autowired
    private MemberProviderRepo memberProviderRepo;
	
	@Autowired
	private MemberClaimServicesRepo memberClaimServicesRepo;
	
	@Autowired
	private MemberClaimRepo memberClaimRepo;
	
	@Autowired
	private ContractMemberClaimsRepo contractMemberClaimsRepo; 
	
	private ContractMemberClaimsEntity contractMemberClaimsEntity;
	
	private String getPCPValidationMessage(PCPValidateResponse pcpValidateResponse) {
		String pcpValidationMessage = null;
		if(pcpValidateResponse != null) {
			List<PCPResponse> pcpResponses = pcpValidateResponse.getPcpResponses();
			if(!pcpResponses.isEmpty()) {
				for (PCPResponse pcpResponse : pcpResponses) {
					List<EnrolleeDetail> enrollees = pcpResponse.getEnrollees();
					for (EnrolleeDetail enrolleeDetail : enrollees) {
						List<String> errorMessages = enrolleeDetail.getErrorMessages();
						boolean pcpValidationFlag = errorMessages.stream().anyMatch(error -> StringUtils.equals(StringUtils.trimToEmpty(error), StringUtils.trimToEmpty(PCP_VALID_FOR_ENROLLEE)));
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
	
	private PCPValidateResponse callPCPValidate(ContractMemberClaimsEntity contractMemberClaimsEntity, MemberClaimEntity memberClaimEntity, String pcpEffectiveDate) throws ServiceException {
		PcpValidateRequest pcpValidateRequest = PcpValidateRequest.builder()
				.contractId(contractMemberClaimsEntity.getContractId())
				.lookAheadDays(LOOK_A_HEAD_DAYS_90)
				.memberType(contractMemberClaimsEntity.getMemberId())
				.mtvPersonId(memberClaimEntity.getPersonId())
				.pcpEffDate(pcpEffectiveDate)
				.product(DC_PRODUCT)
				.pcpEndDate(PCP_END_DATE_12_31_9999)
				.providerId(contractMemberClaimsEntity.getProviderId())
				.recordIdentifier(String.valueOf(random()))
				.sourceSystem(DCM_SOURCESYSTEM)
				.build();
		try {
			PCPValidateResponse pcpValidateResponse = pcpSearchService.pcpValidate(pcpValidateRequest);
			return pcpValidateResponse;
		} catch (Exception e) {
			throw PCPCalculationServiceErrors.PCP_SEARCH_SERVICE_ERROR.createException(e.getMessage());
		}
	}

	private ProviderAssignmentRequest buildProviderAssignment(ContractMemberClaimsEntity contractMemberClaimsEntity, MemberClaimEntity memberClaimEntity, String pcpEffectiveDate) {
		ProviderAssignmentRequest providerAssignmentRequest = ProviderAssignmentRequest.builder()
																.contractID(contractMemberClaimsEntity.getContractId())
																.enrolleeNumber(contractMemberClaimsEntity.getMemberId())
																.pcpEffectiveDate(pcpEffectiveDate)
																.personID(memberClaimEntity.getPersonId())
																.providerContFlag("N")
																.providerID(contractMemberClaimsEntity.getProviderId())
																.reasonCode(REASON_CODE_5NEW)
																.sourceSystem(DCM_SOURCESYSTEM)
																.userId(OPERATORID_PCPCALS)
																.build();
		return providerAssignmentRequest;
	}

	private MemberClaimEntity saveMemberClaimEntity(ContractMemberClaimsEntity contractMemberClaimsEntity, MemberClaimResponse memberClaimResponse) {		
		MemberClaimEntity memberClaimEntity = MemberClaimEntity.builder()
				.billingProvId(memberClaimResponse.getBillingProvId())
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
				.paidTs(getTimestamp(memberClaimResponse.getPaidTs().getNanos()))
				.personId(memberClaimResponse.getPersonId())
				.receivedTs(getTimestamp(memberClaimResponse.getReceivedTs().getNanos()))
				.resolvedTs(getTimestamp(memberClaimResponse.getResolvedTs().getNanos()))
				.servicesNumber(memberClaimResponse.getServicesNumber())
				.contractMemberClaimsId(contractMemberClaimsEntity.getContractMemberClaimId())
				.operatorId(OPERATORID_PCPCALS)
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
						.servicePaidTs(getTimestamp(serviceLine.getServicePaidTs().getNanos()))
						.serviceResolutionTs(getTimestamp(serviceLine.getServiceResolutionTs().getNanos()))
						.memberClaimId(memberClaimEntity.getMemberClaimId())
						.operatorId(OPERATORID_PCPCALS)
						.build();
				memberClaimServicesRepo.save(memberClaimServicesEntity);
			});
		}
	}
	
	private MemberProviderEntity saveMemberProvider(Integer contractMemberClaimsId, String claimStatus, String pcpEffectiveDate) {
		MemberProviderEntity memberProviderEntity = MemberProviderEntity.builder()
				.claimStatus(claimStatus)
				.pcpEffectiveDate(pcpEffectiveDate)
				.reasonCd(REASON_CODE_5NEW)
				.sourceSystem(DCM_SOURCESYSTEM)
				.status(PCP_STATUS_INITIAL)
				.operatorId(OPERATORID_PCPCALS)
				.contractMemberClaimsId(contractMemberClaimsId)
				.build();
		memberProviderRepo.save(memberProviderEntity);
		return memberProviderEntity;
	}
	
	private Timestamp getTimestamp(int nanos) {
		Timestamp ts=new Timestamp(nanos);  
		return ts;
	}
	
	private int random() {
		Random rand = new Random();
		int maxNumber = 10;

		int randomNumber = rand.nextInt(maxNumber) + 1;
		return randomNumber;
	}
	
	private String calculatePCPEffectiveDate() {
		ZoneId defaultZoneId = ZoneId.systemDefault();
		final DateFormat fmt = new SimpleDateFormat("MM-dd-yyyy");
		int currentDateDay = LocalDate.now().getDayOfMonth();
        if (currentDateDay >= 1 && currentDateDay < 16) {
        	LocalDate firstDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        	Date firstDateOfMonth = Date.from(firstDayOfMonth.atStartOfDay(defaultZoneId).toInstant());
        	return fmt.format(firstDateOfMonth);
        } else {
        	LocalDate firstDayOfNextMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
        	Date firstDateOfNextMonth = Date.from(firstDayOfNextMonth.atStartOfDay(defaultZoneId).toInstant());
        	return fmt.format(firstDateOfNextMonth);
        }
	}

	public void processPCPAssignment(ContractMemberClaimsEntity contractMemberClaimsEntity) {
		log.info("START PCPCalculationService.processPCPAssignment processing "+contractMemberClaimsEntity.toString());
		String pcpEffectiveDate = calculatePCPEffectiveDate();
		String errorMessage = null;
		String status = STATUS.VALIDATED.getStatus();
		try {
			MemberClaimRequest memberClaimRequest = MemberClaimRequest.builder().memberClaimId(contractMemberClaimsEntity.getClaimId()).build();
			MemberClaimResponse memberClaimResponse = mtvSyncService.memberClaim(memberClaimRequest);
			if (null != memberClaimResponse && (memberClaimResponse.getErrorCode() == null || memberClaimResponse.getErrorMessage() == null)) {
				List<ServiceLine> serviceLines = memberClaimResponse.getServiceLines();
				if (CollectionUtils.isNotEmpty(serviceLines)) {
					MemberClaimEntity memberClaimEntity = saveMemberClaimEntity(contractMemberClaimsEntity, memberClaimResponse);
					saveMemberClaimServices(memberClaimEntity, serviceLines);
					boolean isExplanationCodeValid = pcpConfigData.isExplanationCodeValid(serviceLines);
					boolean isProcedureCodeValid = pcpConfigData.isProcedureCodeValid(serviceLines);
					boolean isClaimStatusValid = pcpConfigData.isClaimStatusValid(StringUtils.trimToNull(memberClaimEntity.getClaimStatus()));	
					if (isClaimStatusValid && isExplanationCodeValid && isProcedureCodeValid) {
						try {
							PCPValidateResponse pcpValidateResponse = callPCPValidate(contractMemberClaimsEntity, memberClaimEntity, pcpEffectiveDate);
							String pcpValidationMessage = getPCPValidationMessage(pcpValidateResponse);	
							log.info("PCP Validation message for claim id {} is {}.", contractMemberClaimsEntity.getClaimId() , pcpValidationMessage);
							if (StringUtils.equals(pcpValidateResponse.getProcessStatusCode(), PCP_VALIDATION_SUCCESS)
									&& StringUtils.equals(StringUtils.trimToEmpty(pcpValidationMessage), StringUtils.trimToEmpty(PCP_VALID_FOR_ENROLLEE))) {
								MemberProviderEntity memberProviderEntity = saveMemberProvider(contractMemberClaimsEntity.getContractMemberClaimId(), memberClaimEntity.getClaimStatus(), pcpEffectiveDate);
								ProviderAssignmentRequest providerAssignmentRequest = buildProviderAssignment(contractMemberClaimsEntity, memberClaimEntity, pcpEffectiveDate);
								try {
									ProviderAssignmentResponse providerAssignmentResponse = mtvSyncService.providerAssignment(providerAssignmentRequest);
									if (StringUtils.equals(providerAssignmentResponse.getReturnCode(), PCP_ASSIGNMENT_OK)) {
										status = STATUS.PCP_ASSIGNED.getStatus();
										memberProviderRepo.setStatus(memberProviderEntity.getMemberProviderId(), status);
										log.info("PCP Assignment status for claim id {} is {}.", contractMemberClaimsEntity.getClaimId() , status);
									} else {
										errorMessage = providerAssignmentResponse.getErrorMessage();
										if(StringUtils.equals(errorMessage, "string")) {
											errorMessage = "pcp update service provider assignmnet returned error, please check logs.";
										}
										status = STATUS.ERROR.getStatus();
										errorMessage = String.join(":", providerAssignmentResponse.getErrorCode() , providerAssignmentResponse.getErrorMessage());
										memberProviderRepo.setStatus(memberProviderEntity.getMemberProviderId(), errorMessage);
										log.info("PCP Assignment status for claim id {} is {}.", contractMemberClaimsEntity.getClaimId() , status);
									}
								} catch (Exception e) {
									String stacktrace = ExceptionUtils.getStackTrace(e);
									log.error("Exception occured during provider assignment from metavance sync Service.", stacktrace);
									errorMessage = "Exception occured during provider assignment from metavance sync Service.";
									status = STATUS.RETRY.getStatus();
								}
							} else {
								errorMessage = pcpValidationMessage;
								status = STATUS.ERROR.getStatus();
							}
						} catch (Exception e) {
							String stacktrace = ExceptionUtils.getStackTrace(e);
							log.error("Exception occured during pcp valiation from pcp search Service.", stacktrace);
							errorMessage = "Exception occured during pcp valiation from pcp search Service.";
							status = STATUS.RETRY.getStatus();
						}
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
						status = STATUS.ERROR.getStatus();
						log.info("PCP Assignment status for claim id {} is {}.", contractMemberClaimsEntity.getClaimId() , errorMessage);
					}
				} else {
					errorMessage = String.format("Service Line Items are empty for claim# %s ",contractMemberClaimsEntity.getClaimId());
					status = STATUS.ERROR.getStatus();
				}
			} else {
				if(memberClaimResponse == null) {
					errorMessage = String.format("Claim information not found for claim# ", contractMemberClaimsEntity.getClaimId());
				} else {
					errorMessage = String.join(" : ", memberClaimResponse.getErrorCode(), memberClaimResponse.getErrorMessage());
				}
				status = STATUS.ERROR.getStatus();	
			}
		} catch (Exception e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			log.error("Exception occured during retriving member claim information from Metavance Sync Service.", stacktrace);
			errorMessage = "Exception occured during retriving member claim information from Metavance Sync Service.";
			status = STATUS.RETRY.getStatus();
		}
		contractMemberClaimsEntity.setErrorMessage(errorMessage);
		contractMemberClaimsEntity.setStatus(status);
		contractMemberClaimsRepo.save(contractMemberClaimsEntity);
		log.info("END PCPCalculationService.processPCPAssignment");
	}

	@Override
	public void run() {
		processPCPAssignment(contractMemberClaimsEntity);		
	}
}
