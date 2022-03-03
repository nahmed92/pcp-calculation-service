package com.deltadental.pcp.calculation.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.deltadental.mtv.sync.service.MTVSyncService;
import com.deltadental.mtv.sync.service.MemberClaimRequest;
import com.deltadental.mtv.sync.service.MemberClaimResponse;
import com.deltadental.mtv.sync.service.ProviderAssignmentRequest;
import com.deltadental.mtv.sync.service.ProviderAssignmentResponse;
import com.deltadental.mtv.sync.service.ServiceLine;
import com.deltadental.pcp.calculation.controller.Contract;
import com.deltadental.pcp.calculation.domain.ValidateProviderRequest;
import com.deltadental.pcp.calculation.domain.ValidateProviderResponse;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimServicesEntity;
import com.deltadental.pcp.calculation.entities.MemberProviderEntity;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimsRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimServicesRepo;
import com.deltadental.pcp.calculation.repos.MemberProviderRepo;
import com.deltadental.pcp.search.service.PCPSearchService;
import com.deltadental.pcp.search.service.PCPValidateResponse;
import com.deltadental.pcp.search.service.PcpValidateRequest;
import com.deltadental.pcp.search.service.pojos.EnrolleeDetail;
import com.deltadental.pcp.search.service.pojos.PCPResponse;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@NoArgsConstructor
@Slf4j
public class PCPCalculationService {

	private static final String PCP_STATUS_INITIAL = "INITIAL";

	private static final String REASON_CODE_5NEW = "5NEW";

	private static final String LOOK_A_HEAD_DAYS_90 = "90";

	private static final String DCM_SOURCESYSTEM = "DCM";

	private static final String DC_PRODUCT = "DC";
	
	public static String PCP_VALID_FOR_ENROLLEE = " Input PCP is Valid for the Enrollee "; 

	@Autowired
	private PCPSearchService pcpSearchService;
	
	@Autowired
	@Qualifier("mtvSyncService")
	private MTVSyncService mtvSyncService;
	
//	@Autowired
//	private PCPConfigService pcpConfigService;

    @Autowired
    private MemberProviderRepo memberProviderRepo;
	
	@Autowired
	private MemberClaimServicesRepo memberClaimServicesRepo;
	
	@Autowired
	private MemberClaimRepo memberClaimRepo;
	
	@Autowired
	private ContractMemberClaimsRepo contractMemberClaimsRepo; 

//	@Autowired
//	private ObjectMapper mapper;

	
	
	public ValidateProviderResponse validateProvider(ValidateProviderRequest validateProviderRequest) {
		log.info("START PCPCalculationService.validateProvider");
		ValidateProviderResponse validateProviderResponse = new ValidateProviderResponse();
		// Step#1
		List<ContractMemberClaimsEntity> contractMemberClaimsEntities = contractMemberClaimsRepo.findByStatus(null);
		if (contractMemberClaimsEntities != null && !contractMemberClaimsEntities.isEmpty()) {
			contractMemberClaimsEntities.forEach(contractMemberClaim -> {
				// Step# 3 : Retrive member claim information in member_claim_service table
				MemberClaimRequest memberClaimRequest = MemberClaimRequest.builder()
						.memberClaimId(contractMemberClaim.getClaimId())
						.build();
				MemberClaimResponse memberClaimResponse = mtvSyncService.memberClaim(memberClaimRequest);
				MemberClaimEntity memberClaimEntity = null;
				if(null != memberClaimResponse && (memberClaimResponse.getErrorCode() == null || memberClaimResponse.getErrorMessage() == null )) {
					memberClaimEntity = saveMemberClaimEntity(memberClaimResponse);
					if(!memberClaimResponse.getServiceLines().isEmpty()) {
						saveMemberClaimServices(memberClaimEntity, memberClaimResponse.getServiceLines());
					}					
				}

//				// TODO : Step# 4 : Read pcp config service -- read only place holder
				
				// Step# 6 : Validate provider - new end point from pcp search service
				PCPValidateResponse pcpValidateResponse = callPCPValidate(memberClaimEntity);
				String pcpValidationMessage = getPCPValidationMessage(pcpValidateResponse);
				// Retrieve the error message from pcpValidateResponse and validate the error messages
				// Step# 7 : If provider is validated successfully make a call to mtv sync to update pcp
				String validateProviderMessage = null;
				if (StringUtils.equals(pcpValidateResponse.getProcessStatusCode(), "Success") && StringUtils.equals(StringUtils.trimToEmpty(pcpValidationMessage), StringUtils.trimToEmpty(PCP_VALID_FOR_ENROLLEE))) {
					MemberProviderEntity memberProviderEntity = saveMemberProvider(memberClaimEntity);
					ProviderAssignmentRequest providerAssignmentRequest = buildProviderAssignment(memberClaimEntity);
					ProviderAssignmentResponse providerAssignmentResponse = mtvSyncService.providerAssignment(providerAssignmentRequest);
					if (StringUtils.equals(providerAssignmentResponse.getReturnCode(), "OK")) {
						validateProviderMessage = "PROCESSED";
						memberProviderRepo.setStatus(memberProviderEntity.getId(), "PCP ASSIGNED");
					} else {
						validateProviderMessage = providerAssignmentResponse.getErrorMessage();
						memberProviderRepo.setStatus(memberProviderEntity.getId(), "PCP ASSIGNMENT FAILED");
					}
				} else {
					validateProviderMessage = pcpValidationMessage;
				}
				// Step# 8 - update contract_member_claims table back with status
				// processed/unprocessed
				ContractMemberClaimsEntity contractMemberClaimsEntity = contractMemberClaimsRepo.findByClaimId(contractMemberClaim.getClaimId());
				contractMemberClaimsEntity.setStatus(validateProviderMessage);
				contractMemberClaimsRepo.save(contractMemberClaimsEntity);
				contractMemberClaimsRepo.setStatus(contractMemberClaimsEntity.getId(), validateProviderMessage);				
			});
		}
		return validateProviderResponse;
	}

	private String getPCPValidationMessage(PCPValidateResponse pcpValidateResponse) {
		String pcpValidationMessage = "";
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
//							pcpValidationMessage = errorMessages.get(0);
							pcpValidationMessage = StringUtils.trimToEmpty(PCP_VALID_FOR_ENROLLEE);
						}
					}
				}
			}
		}
		return pcpValidationMessage;
	}

	private PCPValidateResponse callPCPValidate(MemberClaimEntity memberClaimEntity) {
		PcpValidateRequest pcpValidateRequest = PcpValidateRequest.builder()
				.contractId(memberClaimEntity.getContractId())
				.lookAheadDays(LOOK_A_HEAD_DAYS_90)
				.memberType(memberClaimEntity.getMemberID())
				.mtvPersonId(memberClaimEntity.getPersonId())
				.pcpEffDate(calculatePCPEffectiveDate())
				.product(DC_PRODUCT)
				.pcpEndDate(null)
				.providerId(memberClaimEntity.getProviderId())
				.recordIdentifier(String.valueOf(random()))
				.sourceSystem(DCM_SOURCESYSTEM)
				.build();
		PCPValidateResponse pcpValidateResponse = pcpSearchService.pcpValidate(pcpValidateRequest);
		return pcpValidateResponse;
	}

	private ProviderAssignmentRequest buildProviderAssignment(MemberClaimEntity memberClaimEntity) {
		ProviderAssignmentRequest providerAssignmentRequest = ProviderAssignmentRequest.builder()
																.contractID(memberClaimEntity.getContractId())
																.enrolleeNumber(memberClaimEntity.getMemberID())
																.pcpEffectiveDate(calculatePCPEffectiveDate())
																.pcpEndDate(null)
																.personID(memberClaimEntity.getPersonId())
//																.practiceLocation(memberClaimServiceEntity.getPracticeLocationNumber())
																.providerContFlag("N")
																.providerID(memberClaimEntity.getProviderId())
																.reasonCode(REASON_CODE_5NEW)
																.sourceSystem(DCM_SOURCESYSTEM)
																.build();
		return providerAssignmentRequest;
	}

	private MemberClaimEntity saveMemberClaimEntity(MemberClaimResponse memberClaimResponse) {		
		MemberClaimEntity memberClaimEntity = MemberClaimEntity.builder()
				.billingProvId(memberClaimResponse.getBillingProvId())
				.businessLevel4(memberClaimResponse.getBusinessLevel4())
				.businessLevel5(memberClaimResponse.getBusinessLevel5())
				.businessLevel6(memberClaimResponse.getBusinessLevel6())
				.businessLevel7(memberClaimResponse.getBusinessLevel7())
				.claimId(memberClaimResponse.getClaimId())
				.claimSource(memberClaimResponse.getClaimSource())
				.claimStatus(memberClaimResponse.getClaimStatus())
				.claimType(memberClaimResponse.getClaimType())
				.contractId(memberClaimResponse.getContractId())
				.groupNumber(memberClaimResponse.getGroupNumber())
				.memberFirstName(memberClaimResponse.getMemberFirstName())
				.memberID(memberClaimResponse.getMemberID())
				.memberLastName(memberClaimResponse.getMemberLastName())
				.paidTs(getTimestamp(memberClaimResponse.getPaidTs().getNanos()))
				.personId(memberClaimResponse.getPersonId())
				.providerId(memberClaimResponse.getProviderId())
				.receivedTs(getTimestamp(memberClaimResponse.getReceivedTs().getNanos()))
				.resolvedTs(getTimestamp(memberClaimResponse.getResolvedTs().getNanos()))
				.servicesNumber(memberClaimResponse.getServicesNumber())
				.build();
		memberClaimRepo.save(memberClaimEntity);
		return memberClaimEntity;
	}
	
	private void saveMemberClaimServices(MemberClaimEntity memberClaimEntity, List<ServiceLine> serviceLines) {
		if(!serviceLines.isEmpty()) {
			serviceLines.forEach(serviceLine -> {
				MemberClaimServicesEntity memberClaimServicesEntity = MemberClaimServicesEntity.builder()
						.claimId(memberClaimEntity.getClaimId())
						.claimType(serviceLine.getClaimType())
						.contractId(memberClaimEntity.getContractId())
						.encounter_flag(serviceLine.getEncounterFlag())
						.explanationCode(serviceLine.getExplnCode())
						.providerId(memberClaimEntity.getProviderId())
						.memberId(memberClaimEntity.getMemberID())
						.procedureCode(serviceLine.getProcedureCode())
						.sequenceNumber(serviceLine.getSequenceNumber())
						.serviceNumber(serviceLine.getServiceNumber())
						.servicePaidTs(getTimestamp(serviceLine.getServicePaidTs().getNanos()))
						.serviceResolutionTs(getTimestamp(serviceLine.getServiceResolutionTs().getNanos()))
						.build();
				memberClaimServicesRepo.save(memberClaimServicesEntity);
			});
		}
	}
	
	private MemberProviderEntity saveMemberProvider(MemberClaimEntity memberClaimEntity) {
		MemberProviderEntity memberProviderEntity = MemberProviderEntity.builder()
				.claimId(memberClaimEntity.getClaimId())
				.claimStatus(memberClaimEntity.getClaimStatus())
				.contractId(memberClaimEntity.getContractId())
				.memberId(memberClaimEntity.getMemberID())
				.pcpEffectiveDate(calculatePCPEffectiveDate())
				.pcpIdentifier(memberClaimEntity.getProviderId())
				.reasonCd(REASON_CODE_5NEW)
				.sourceSystem(DCM_SOURCESYSTEM)
				.status(PCP_STATUS_INITIAL)
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
		int maxNumber = 2;

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

	public List<Contract> setAssginmentDate(List<Contract> contracts) {
		contracts.stream().forEach(contract -> {
			int currentDateDay = LocalDate.now().getDayOfMonth();
			if (currentDateDay >= 1 && currentDateDay <= 16) {
				contract.setAssignmentDate(Timestamp.valueOf(LocalDateTime.now().withDayOfMonth(1)));
				log.info("setting assignment Date..{}", Timestamp.valueOf(LocalDateTime.now().withDayOfMonth(1)));
			} else {
				contract.setAssignmentDate(Timestamp.valueOf(LocalDateTime.now().with(TemporalAdjusters.firstDayOfNextMonth())));
				log.info("setting assignment Date..{}", Timestamp.valueOf(LocalDateTime.now().with(TemporalAdjusters.firstDayOfNextMonth())));
			}
		});
		return contracts;
	}

	public ValidateProviderResponse assignMemberPCP(ValidateProviderRequest validateProviderRequest) {
		log.info("START PCPCalculationService.assignMemberPCP");
		ValidateProviderResponse validateProviderResponse = new ValidateProviderResponse();
		ContractMemberClaimsEntity contractMemberClaimsEntity = ContractMemberClaimsEntity.builder()
				.claimId(validateProviderRequest.getClaimId()).contractId(validateProviderRequest.getContractId())
				.memberId(validateProviderRequest.getMemberId()).providerId(validateProviderRequest.getProviderId())
				.state(validateProviderRequest.getState()).operatorId("PCP-INGESTION-SERVICE").build();
		contractMemberClaimsRepo.save(contractMemberClaimsEntity);
		contractMemberClaimsRepo.flush();
		MemberClaimRequest memberClaimRequest = MemberClaimRequest.builder()
				.memberClaimId(contractMemberClaimsEntity.getClaimId()).build();
		MemberClaimResponse memberClaimResponse = mtvSyncService.memberClaim(memberClaimRequest);
		MemberClaimEntity memberClaimEntity = null;
		if (null != memberClaimResponse
				&& (memberClaimResponse.getErrorCode() == null || memberClaimResponse.getErrorMessage() == null)) {
			memberClaimEntity = saveMemberClaimEntity(memberClaimResponse);
			if (!memberClaimResponse.getServiceLines().isEmpty()) {
				saveMemberClaimServices(memberClaimEntity, memberClaimResponse.getServiceLines());
			}
		}

		PCPValidateResponse pcpValidateResponse = callPCPValidate(memberClaimEntity);
		String pcpValidationMessage = getPCPValidationMessage(pcpValidateResponse);

		String validateProviderMessage = null;
		if (StringUtils.equals(pcpValidateResponse.getProcessStatusCode(), "Success") && StringUtils.equals(
				StringUtils.trimToEmpty(pcpValidationMessage), StringUtils.trimToEmpty(PCP_VALID_FOR_ENROLLEE))) {
			MemberProviderEntity memberProviderEntity = saveMemberProvider(memberClaimEntity);
			ProviderAssignmentRequest providerAssignmentRequest = buildProviderAssignment(memberClaimEntity);
			ProviderAssignmentResponse providerAssignmentResponse = mtvSyncService
					.providerAssignment(providerAssignmentRequest);
			if (StringUtils.equals(providerAssignmentResponse.getReturnCode(), "OK")) {
				validateProviderMessage = "PROCESSED";
				memberProviderRepo.setStatus(memberProviderEntity.getId(), "PCP ASSIGNED");
			} else {
				validateProviderMessage = providerAssignmentResponse.getErrorMessage();
				memberProviderRepo.setStatus(memberProviderEntity.getId(), "PCP ASSIGNMENT FAILED");
			}
		} else {
			validateProviderMessage = pcpValidationMessage;
		}

		contractMemberClaimsEntity.setStatus(validateProviderMessage);
		// contractMemberClaimsRepo.save(contractMemberClaimsEntity);
		contractMemberClaimsRepo.setStatus(contractMemberClaimsEntity.getId(), validateProviderMessage);
		validateProviderResponse.setPcpEffectiveDate(calculatePCPEffectiveDate());
		validateProviderResponse.setStatus(validateProviderMessage);
		log.info("END PCPCalculationService.assignMemberPCP");
		return validateProviderResponse;
	}
}
