package com.deltadental.pcp.calculation.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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
import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.domain.MemberContractClaimResponse;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimServicesEntity;
import com.deltadental.pcp.calculation.entities.MemberProviderEntity;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimsRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimServicesRepo;
import com.deltadental.pcp.calculation.repos.MemberProviderRepo;
import com.deltadental.pcp.config.service.PCPConfigService;
import com.deltadental.pcp.config.service.PcpConfigResponse;
import com.deltadental.pcp.search.service.PCPSearchService;
import com.deltadental.pcp.search.service.PCPValidateResponse;
import com.deltadental.pcp.search.service.PcpValidateRequest;
import com.deltadental.pcp.search.service.pojos.EnrolleeDetail;
import com.deltadental.pcp.search.service.pojos.PCPResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@NoArgsConstructor
@Slf4j
public class PCPCalculationService {

	private static final String MEMBER_CONTRACT_CLAIM_PROCESSED = "PROCESSED";

	private static final String PCP_ASSIGNMENT_FAILED = "PCP ASSIGNMENT FAILED";

	private static final String PCP_ASSIGNED = "PCP ASSIGNED";

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
	@Qualifier("mtvSyncService")
	private MTVSyncService mtvSyncService;
	
	@Autowired
	private PCPConfigService pcpConfigService;

    @Autowired
    private MemberProviderRepo memberProviderRepo;
	
	@Autowired
	private MemberClaimServicesRepo memberClaimServicesRepo;
	
	@Autowired
	private MemberClaimRepo memberClaimRepo;
	
	@Autowired
	private ContractMemberClaimsRepo contractMemberClaimsRepo; 
	
	@Autowired
	private ObjectMapper objectMapper;

	public MemberContractClaimResponse assignPCPsToMembers() {
		log.info("START PCPCalculationService.validateProvider");
		MemberContractClaimResponse validateProviderResponse = new MemberContractClaimResponse();
		// Step#1
		List<ContractMemberClaimsEntity> contractMemberClaimsEntities = contractMemberClaimsRepo.findByStatus(null);
		if (contractMemberClaimsEntities != null && !contractMemberClaimsEntities.isEmpty()) {
			contractMemberClaimsEntities.forEach(contractMemberClaim -> {
				processPCPAssignment(validateProviderResponse, contractMemberClaim);
			});
		}
		return validateProviderResponse;
	}
	
	public void stageMemberContractClaimRecord(MemberContractClaimRequest validateProviderRequest) {
		log.info("START PCPCalculationService.assignMemberPCP");
		saveContractMemberClaims(validateProviderRequest);
		log.info("END PCPCalculationService.assignMemberPCP");
	}

	public void stageMemberContractClaimRecords(List<MemberContractClaimRequest> validateProviderRequests) {
		log.info("START PCPCalculationService.assignMemberPCP");
		if(null != validateProviderRequests && !validateProviderRequests.isEmpty()) {
			validateProviderRequests.forEach(validateProviderRequest -> saveContractMemberClaims(validateProviderRequest));
		}
		log.info("END PCPCalculationService.assignMemberPCP");
	}

	
	private void saveContractMemberClaims(MemberContractClaimRequest validateProviderRequest) {
		ContractMemberClaimsEntity contractMemberClaimsEntity = ContractMemberClaimsEntity.builder()
				.claimId(StringUtils.trimToNull(validateProviderRequest.getClaimId()))
				.contractId(StringUtils.trimToNull(validateProviderRequest.getContractId()))
				.memberId(StringUtils.trimToNull(validateProviderRequest.getMemberId()))
				.providerId(StringUtils.trimToNull(validateProviderRequest.getProviderId()))
				.state(StringUtils.trimToNull(validateProviderRequest.getState()))
				.operatorId(StringUtils.trimToNull(validateProviderRequest.getOperatorId()))
				.build();
		contractMemberClaimsRepo.save(contractMemberClaimsEntity);
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
							pcpValidationMessage = errorMessages.get(0);
						}
					}
				}
			}
		}
		return pcpValidationMessage;
	}

	private PCPValidateResponse callPCPValidate(MemberClaimEntity memberClaimEntity, String pcpEffectiveDate) {
		PcpValidateRequest pcpValidateRequest = PcpValidateRequest.builder()
				.contractId(memberClaimEntity.getContractId())
				.lookAheadDays(LOOK_A_HEAD_DAYS_90)
				.memberType(memberClaimEntity.getMemberID())
				.mtvPersonId(memberClaimEntity.getPersonId())
				.pcpEffDate(pcpEffectiveDate)
				.product(DC_PRODUCT)
				.pcpEndDate(PCP_END_DATE_12_31_9999)
				.providerId(memberClaimEntity.getProviderId())
				.recordIdentifier(String.valueOf(random()))
				.sourceSystem(DCM_SOURCESYSTEM)
				.build();
		PCPValidateResponse pcpValidateResponse = pcpSearchService.pcpValidate(pcpValidateRequest);
		return pcpValidateResponse;
	}

	private ProviderAssignmentRequest buildProviderAssignment(MemberClaimEntity memberClaimEntity, String pcpEffectiveDate) {
		ProviderAssignmentRequest providerAssignmentRequest = ProviderAssignmentRequest.builder()
																.contractID(memberClaimEntity.getContractId())
																.enrolleeNumber(memberClaimEntity.getMemberID())
																.pcpEffectiveDate(pcpEffectiveDate)
																.pcpEndDate(PCP_END_DATE_12_31_9999)
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
	
	private MemberProviderEntity saveMemberProvider(MemberClaimEntity memberClaimEntity, String pcpEffectiveDate) {
		MemberProviderEntity memberProviderEntity = MemberProviderEntity.builder()
				.claimId(memberClaimEntity.getClaimId())
				.claimStatus(memberClaimEntity.getClaimStatus())
				.contractId(memberClaimEntity.getContractId())
				.memberId(memberClaimEntity.getMemberID())
				.pcpEffectiveDate(pcpEffectiveDate)
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


	private void processPCPAssignment(MemberContractClaimResponse validateProviderResponse, ContractMemberClaimsEntity contractMemberClaimsEntity) {
		String validateProviderMessage = null;
		String pcpEffectiveDate = calculatePCPEffectiveDate();
		try {
			MemberClaimRequest memberClaimRequest = MemberClaimRequest.builder().memberClaimId(contractMemberClaimsEntity.getClaimId()).build();
			MemberClaimResponse memberClaimResponse = mtvSyncService.memberClaim(memberClaimRequest);
			if (null != memberClaimResponse && (memberClaimResponse.getErrorCode() == null || memberClaimResponse.getErrorMessage() == null)) {
				List<ServiceLine> serviceLines = memberClaimResponse.getServiceLines();
				if (serviceLines != null && !serviceLines.isEmpty()) {
					MemberClaimEntity memberClaimEntity = saveMemberClaimEntity(memberClaimResponse);
					saveMemberClaimServices(memberClaimEntity, serviceLines);
					boolean isExplanationCodeValid = isExplanationCodeValid(serviceLines);
					boolean isProcedureCodeValid = isProcedureCodeValid(serviceLines);
					boolean isClaimStatusValid = isClaimStatusValid(StringUtils.trimToNull(memberClaimEntity.getClaimStatus()));
	
					if (isClaimStatusValid && isExplanationCodeValid && isProcedureCodeValid) {
						PCPValidateResponse pcpValidateResponse = callPCPValidate(memberClaimEntity, pcpEffectiveDate);
						String pcpValidationMessage = getPCPValidationMessage(pcpValidateResponse);
	
						if (StringUtils.equals(pcpValidateResponse.getProcessStatusCode(), PCP_VALIDATION_SUCCESS)
								&& StringUtils.equals(StringUtils.trimToEmpty(pcpValidationMessage), StringUtils.trimToEmpty(PCP_VALID_FOR_ENROLLEE))) {
							MemberProviderEntity memberProviderEntity = saveMemberProvider(memberClaimEntity, pcpEffectiveDate);
							ProviderAssignmentRequest providerAssignmentRequest = buildProviderAssignment(memberClaimEntity, pcpEffectiveDate);
							ProviderAssignmentResponse providerAssignmentResponse = mtvSyncService.providerAssignment(providerAssignmentRequest);
							if (StringUtils.equals(providerAssignmentResponse.getReturnCode(), PCP_ASSIGNMENT_OK)) {
								validateProviderMessage = MEMBER_CONTRACT_CLAIM_PROCESSED;
								memberProviderRepo.setStatus(memberProviderEntity.getId(), PCP_ASSIGNED);
							} else {
								validateProviderMessage = providerAssignmentResponse.getErrorMessage();
								memberProviderRepo.setStatus(memberProviderEntity.getId(), PCP_ASSIGNMENT_FAILED);
							}
						} else {
							validateProviderMessage = pcpValidationMessage;
						}
					} else {
						if (!isClaimStatusValid) {
							validateProviderMessage = "Claim status is not valid to proceed for PCP assignment!";
						}
	
						if (!isExplanationCodeValid) {
							if (StringUtils.isNotBlank(validateProviderMessage)) {
								validateProviderMessage = String.join(", ", validateProviderMessage,
										"One of the Service Line Explanation Code is not valid for this claim!");
							} else {
								validateProviderMessage = "One of the Service Line Explanation Code is not valid for this claim!";
							}
						}
	
						if (!isProcedureCodeValid) {
							if (StringUtils.isNotBlank(validateProviderMessage)) {
								validateProviderMessage = String.join(", ", validateProviderMessage, "One of the Service Line Procedure Code is not valid for this claim!");
							} else {
								validateProviderMessage = "One of the Service Line Procedure Code is not valid for this claim!";
							}
						}
					}
				} else {
					validateProviderMessage = "No services are done for this claim information found with the claim id : "+ contractMemberClaimsEntity.getClaimId();
				}
			} else {
				validateProviderMessage = "No claim information found with the claim id : "+ contractMemberClaimsEntity.getClaimId();
			}
		} catch (Exception e) {
			validateProviderMessage = e.getLocalizedMessage();
		}
		contractMemberClaimsEntity.setStatus(validateProviderMessage);
		contractMemberClaimsRepo.setStatus(contractMemberClaimsEntity.getId(), validateProviderMessage);
		validateProviderResponse.setClaimId(contractMemberClaimsEntity.getClaimId());
		validateProviderResponse.setContractId(contractMemberClaimsEntity.getContractId());
		validateProviderResponse.setMemberId(contractMemberClaimsEntity.getMemberId());
		validateProviderResponse.setProviderId(contractMemberClaimsEntity.getProviderId());
		validateProviderResponse.setPcpEffectiveDate(pcpEffectiveDate);
		validateProviderResponse.setStatus(validateProviderMessage);
	}
	
	private boolean isClaimStatusValid(String claimStatus) {
		String jsonClaimStatusStr = pcpConfigService.claimStatus();
		try {
			JsonNode jsonNode = objectMapper.readTree(jsonClaimStatusStr);
			List<PcpConfigResponse> claimStatusList = objectMapper.convertValue(jsonNode, new TypeReference<List<PcpConfigResponse>>() {});
			return claimStatusList.stream().anyMatch(pcpConfigResponse -> StringUtils.equals(pcpConfigResponse.getCodeValue(), claimStatus));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private boolean isExplanationCodeValid(List<ServiceLine> serviceLines) {
		boolean isExplanationCodeValid = false;
		if (serviceLines != null && !serviceLines.isEmpty()) {
			String jsonExplanationCodeStr = pcpConfigService.explanationCode();
			List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonExplanationCodeStr);
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

	private List<PcpConfigResponse> getPcpConfigResponseList(String jsonString)  {
		try {
			JsonNode jsonNode = objectMapper.readTree(jsonString);
			List<PcpConfigResponse> explnCodesList = objectMapper.convertValue(jsonNode, new TypeReference<List<PcpConfigResponse>>() {});
			return explnCodesList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<PcpConfigResponse>();
	}
	
	private boolean isProcedureCodeValid(List<ServiceLine> serviceLines) {
		boolean isProcedureCodeValid = true;
		if (serviceLines != null && !serviceLines.isEmpty()) {
			String jsonProcedureCodeStr = pcpConfigService.procedureCode();
			List<PcpConfigResponse> pcpConfigResponses = getPcpConfigResponseList(jsonProcedureCodeStr);
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
