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

import org.apache.catalina.mapper.Mapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.deltadental.mtv.sync.service.BenefitPackage;
import com.deltadental.mtv.sync.service.CoverageSpan;
import com.deltadental.mtv.sync.service.GroupProdNetwork;
import com.deltadental.mtv.sync.service.MTVSyncService;
import com.deltadental.mtv.sync.service.MemberClaimRequest;
import com.deltadental.mtv.sync.service.MemberClaimResponse;
import com.deltadental.mtv.sync.service.PcpEligbility;
import com.deltadental.mtv.sync.service.RetrieveContract;
import com.deltadental.mtv.sync.service.RetrieveContractResponse;
import com.deltadental.mtv.sync.service.RetrieveEligibilitySummary;
import com.deltadental.mtv.sync.service.RetrieveEligibilitySummaryResponse;
import com.deltadental.mtv.sync.service.UpdatePCP;
import com.deltadental.mtv.sync.service.UpdatePCPRequest;
import com.deltadental.mtv.sync.service.UpdatePCPResponse;
import com.deltadental.pcp.calculation.controller.Contract;
import com.deltadental.pcp.calculation.domain.ValidateProviderRequest;
import com.deltadental.pcp.calculation.domain.ValidateProviderResponse;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;
import com.deltadental.pcp.calculation.entities.MemberClaimServiceEntity;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimsRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimServiceRepo;
import com.deltadental.pcp.calculation.repos.MemberProviderRepo;
import com.deltadental.pcp.config.service.PCPConfigService;
import com.deltadental.pcp.search.service.PCPSearchService;
import com.deltadental.pcp.search.service.PCPValidateResponse;
import com.deltadental.pcp.search.service.PcpValidateRequest;
import com.deltadental.pcp.search.service.pojos.EnrolleeDetail;
import com.deltadental.pcp.search.service.pojos.PCPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@NoArgsConstructor
@Slf4j
public class PCPCalculationService {

	private static final String DCM_SOURCESYSTEM = "DCM";

	private static final String DC_PRODUCT = "DC";

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
	private MemberClaimServiceRepo memberClaimServiceRepo;
	
	@Autowired
	private ContractMemberClaimsRepo contractMemberClaimsRepo; 

//	@Autowired
//	private ObjectMapper mapper;

	public ValidateProviderResponse validateProvider(ValidateProviderRequest validateProviderRequest) {
//		log.info("START PCPCalculationService.validateProvider");
//		// TODO : Call pcp-config-service
//		int lookAHeadDays = 90;
//		String claimStatus = "Y";
//		String procedureStatusCode = "120";
//		ValidateProviderResponse validateProviderResponse = new ValidateProviderResponse();
//		if(StringUtils.equals(claimStatus, "Y") && StringUtils.equals(procedureStatusCode, "120")) {
//			// TODO : Call pcp-search-service
//			String pcpEffectiveDate = calculatePCPEffectiveDate();
//			String providerValidationStatus = "PROVIDER VALIDATED";
//			MemberProviderEntity memberProviderEntity = new MemberProviderEntity();
//			memberProviderEntity.setMemberType(validateProviderRequest.getMemberType());
//			memberProviderEntity.setMemberId(validateProviderRequest.getMemberId());
//			memberProviderEntity.setContractId(validateProviderRequest.getContractId());
//			memberProviderEntity.setPcpIdentifier(validateProviderRequest.getProviderId());
//			memberProviderEntity.setZipCode(validateProviderRequest.getZipCode());
//			memberProviderEntity.setPcpEffectiveDate(pcpEffectiveDate);
//			memberProviderEntity.setClaimStatus(claimStatus);
////			providerValidateEntity.setProcedureStatusCode(procedureStatusCode);
//			memberProviderRepo.save(memberProviderEntity);
//			validateProviderResponse.setPcpEffectiveDate(pcpEffectiveDate);
//			validateProviderResponse.setStatus(providerValidationStatus);
//		} else {
//			
//		}
//		
//		
//		// TODO : Call mtv-sync-service
//
//		
//		// TODO: save record into DB
//		log.info("END PCPCalculationService.validateProvider");
//		return validateProviderResponse;
		
		// Step#1
		List<ContractMemberClaimsEntity> contractMemberClaimsEntities = contractMemberClaimsRepo.findAll();
		if(contractMemberClaimsEntities != null && !contractMemberClaimsEntities.isEmpty()) {
			contractMemberClaimsEntities.forEach(contractMemberClaim -> {
				RetrieveContract retrieveContract = new RetrieveContract();
				retrieveContract.setContractId(contractMemberClaim.getContractId());
				// Step# 2 : call mtv sync service to retrieve contract from MTV sync service
				RetrieveContractResponse retrieveContractResponse =  mtvSyncService.retrieveContract(retrieveContract);
				//Step# 3 : step 3 - retrive member claim information in member_claim_service table
				
				MemberClaimRequest memberClaimRequest = MemberClaimRequest.builder().memberClaimId(contractMemberClaim.getClaimId()).build();
				MemberClaimResponse memberClaimResponse = mtvSyncService.memberClaim(memberClaimRequest);
				
				MemberClaimServiceEntity memberClaimServiceEntity = MemberClaimServiceEntity.builder()
						.claimId(memberClaimResponse.getClaimId())
						.claimStatus(memberClaimResponse.getClaimStatus())
						.contractId(memberClaimResponse.getContractId())
						// value set of contractMemberClaim
						.crationTs(contractMemberClaim.getCrationTs())
						.explanationCode(!memberClaimResponse.getServiceLines().isEmpty()?
								memberClaimResponse.getServiceLines().get(0).getExplnCode():null)
						// value set of contractMemberClaim
						.lastMaintTs(contractMemberClaim.getLastMaintTs())
						.memberId(memberClaimResponse.getMemberID())
						// value set of contractMemberClaim
						.operatorId(contractMemberClaim.getOperatorId())
						.procedureCode(!memberClaimResponse.getServiceLines().isEmpty()?
								memberClaimResponse.getServiceLines().get(0).getProcedureCode():null)
						.procedureStatus(contractMemberClaim.getStatus())
						.providerId(memberClaimResponse.getProviderId())
						.receiveDate(memberClaimResponse.getReceivedTs().toString())						
						.resolvedDate(memberClaimResponse.getResolvedTs().toString())
						// value set of contractMemberClaim
						.state(contractMemberClaim.getState())
						.build();
				memberClaimServiceRepo.save(memberClaimServiceEntity);				
				String validateProviderMessage = null;
				
				// TODO : step 4 - read pcp config service -- read only place holder
//				final String explanationCode = pcpConfigService.explanationCode();
//				final String procedureCode = pcpConfigService.procedureCode();
//				final String claimStatus = pcpConfigService.claimStatus();
				
				// TODO : step 5 - validate procedure, claim status and explanation code
//				if(!StringUtils.equals(memberClaimServiceEntity.getProcedureCode(), procedureCode) && 
//						StringUtils.equals(memberClaimServiceEntity.getExplanationCode(), explanationCode) && 
//						StringUtils.equals(memberClaimServiceEntity.getClaimStatus(), claimStatus)) {
					
					// TODO : step 6 - vaidate provider - new end point from pcp search service
					PcpValidateRequest pcpValidateRequest = PcpValidateRequest.builder()
							.contractId(contractMemberClaim.getContractId())
							.lookAheadDays("90")
							.memberType(contractMemberClaim.getMemberId())
							.mtvPersonId((retrieveContractResponse.getPrimaryEnrollee()!=null
									&& retrieveContractResponse.getPrimaryEnrollee().getPerson()!=null)?
									retrieveContractResponse.getPrimaryEnrollee().getPerson().getPersonIdentfier():null)
							// TODO : get the correct value
							.pcpEffDate(calculatePCPEffectiveDate())
							.pcpEndDate(null)
							.product(DC_PRODUCT)
							.providerId(contractMemberClaim.getProviderId())
							.recordIdentifier(String.valueOf(random()))
							.sourceSystem(DCM_SOURCESYSTEM)
							.build();
    				PCPValidateResponse pcpValidateResponse = validatePcp(pcpValidateRequest);
    				// Retrieve the error message from pcpValidateResponse and validate the error messages
					// TODO : step 7 - if provider is validated successfully make a call to mtv sync to update pcp
					if(pcpValidateResponse.getProcessStatusCode()!=null &&
							pcpValidateResponse.getProcessStatusCode().equals("Success")) {
    				UpdatePCPRequest updatePCP = buildUpdatePCP(retrieveContractResponse, memberClaimServiceEntity);
					UpdatePCPResponse updatePCPResponse = mtvSyncService.updatePCPMember(updatePCP);
					if(StringUtils.equals(updatePCPResponse.getReturnCode(), "OK")) {
						validateProviderMessage = "PROCESSED";
					} else {
						validateProviderMessage = updatePCPResponse.getErrorMessage();
					}
				}
			//	}
				ContractMemberClaimsEntity contractMemberClaimsEntity = contractMemberClaimsRepo.findByClaimId(contractMemberClaim.getClaimId());
				contractMemberClaimsEntity.setStatus(validateProviderMessage);
				contractMemberClaimsRepo.save(contractMemberClaimsEntity);
				contractMemberClaimsRepo.setStatus(contractMemberClaimsEntity.getId(), validateProviderMessage);
				// TODO : step 8 - update contract_member_claims table back with status processed/unprocessed
			});
		}
		return null;		
	}
	
	private int random() {
		Random rand = new Random();
		int maxNumber = 2;

		int randomNumber = rand.nextInt(maxNumber) + 1;
		return randomNumber;
	}
	
	private PCPValidateResponse validatePcp(PcpValidateRequest pcpValidateRequest) {
		PCPValidateResponse pcpValidateResponse = pcpSearchService.pcpValidate(pcpValidateRequest);
		List<PCPResponse> pcpResponses = pcpValidateResponse.getPcpResponses();
		if(pcpResponses != null && !pcpResponses.isEmpty()) {
			for (PCPResponse pcpResponse : pcpResponses) {
				List<EnrolleeDetail> enrollees = pcpResponse.getEnrollees();
				for(EnrolleeDetail enrollee : enrollees) {
					List<String> errorMessages = enrollee.getErrorMessages();
				}
			}
		}
		return pcpValidateResponse;
	}
	
	
	private UpdatePCPRequest buildUpdatePCP(RetrieveContractResponse retrieveContractResponse, MemberClaimServiceEntity memberClaimServiceEntity) {
		List<BenefitPackage> bpList = new ArrayList<BenefitPackage>();
		List<GroupProdNetwork> groupProdNetworks = new ArrayList<GroupProdNetwork>();
		RetrieveEligibilitySummary retrieveEligibilitySummary = new RetrieveEligibilitySummary();
		retrieveEligibilitySummary.setContractId(memberClaimServiceEntity.getContractId());
		RetrieveEligibilitySummaryResponse retrieveEligibilitySummaryResponse =  mtvSyncService.retrieveEligibilitySummary(retrieveEligibilitySummary);
		if(null != retrieveEligibilitySummaryResponse 
				&& null != retrieveEligibilitySummaryResponse
				&& null != retrieveEligibilitySummaryResponse.getPcpEligbility()) {
			//RetrieveEligibilitySummaryReturnResponse retrieveEligibilitySummaryReturnResponse = retrieveEligibilitySummaryResponse.get_return();
			List<PcpEligbility> pcpEligbility = retrieveEligibilitySummaryResponse.getPcpEligbility();
			BenefitPackage bp = BenefitPackage.builder()
					.benefitPackageId(pcpEligbility.get(0).getBenefitPackageId())
					.bpEffectiveDate("01-01-2021")
					.build();
			bpList.add(bp);
			
			GroupProdNetwork groupProdNetwork = GroupProdNetwork.builder()
					.financialReportingStateIdentifier(pcpEligbility.get(0).getFinancialReportingStateIdentifier())
					.gpnaEffectiveDate("01-01-2021")
					.groupTypeIdentifer(pcpEligbility.get(0).getGroupTypeIdentifier())
					.healthCareContractHolderIdentifier(pcpEligbility.get(0).getHealthCareContractHolderIdentifier())
					.networkIdentifier("2DELTACARE")
					.productIdentifier(pcpEligbility.get(0).getProduct())
					.build();
			groupProdNetworks.add(groupProdNetwork);
		}
		List<CoverageSpan> coverageSpans = retrieveContractResponse.getCoverageSpans();
		String divsionNumber = null;
		String groupNumber = null;
		if(!coverageSpans.isEmpty()) {
			CoverageSpan coverageSpan = coverageSpans.get(0);
			divsionNumber = coverageSpan.getDivisionNumber();
			groupNumber = coverageSpan.getGroupNumber();
		}
		UpdatePCPRequest updatePCPRequest = UpdatePCPRequest.builder()
				.contractID(retrieveContractResponse.getContractId())
				.actionIndicator("U")
				.reasonCode("5NEW")
				.pcpEffectiveDate(calculatePCPEffectiveDate())
				.providerID(memberClaimServiceEntity.getProviderId())
				.providerContFlag("N")
				.enrolleeNumber(memberClaimServiceEntity.getMemberId())
				.divsionNumber(divsionNumber)
				.groupNumber(groupNumber)
				//.personID(myReturn.getEnrollee().getPersonId())
				.sourceSystem("BATCH")
				.benefitPackage(bpList)
				.groupProdNetwork(groupProdNetworks)
				.build();
//		UpdatePCP updatePCP = UpdatePCP.builder()
//				.arg0(updatePCPRequest)
//				.build();
		return updatePCPRequest;
		
	}
	
	private MemberClaimServiceEntity getAllMemberClaimServiceEntities(String contractId, String memberId, String providerId, String claimId) {
		MemberClaimServiceEntity memberClaimServiceEntity = memberClaimServiceRepo.findByContractIdAndMemberIdAndProviderIdAndClaimId(contractId, memberId, providerId, claimId);
		return memberClaimServiceEntity;
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
				contract.setAssignmentDate(
						Timestamp.valueOf(LocalDateTime.now().with(TemporalAdjusters.firstDayOfNextMonth())));
				log.info("setting assignment Date..{}",
						Timestamp.valueOf(LocalDateTime.now().with(TemporalAdjusters.firstDayOfNextMonth())));
			}

		});
		return contracts;
	}
}
