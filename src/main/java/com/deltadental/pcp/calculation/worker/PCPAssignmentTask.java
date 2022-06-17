package com.deltadental.pcp.calculation.worker;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.deltadental.mtv.sync.interservice.MTVSyncServiceClient;
import com.deltadental.mtv.sync.interservice.dto.MemberClaimResponse;
import com.deltadental.mtv.sync.interservice.dto.ServiceLine;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.interservice.PCPConfigData;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimRepo;
import com.deltadental.pcp.calculation.repos.MemberClaimServicesRepo;
import com.deltadental.pcp.calculation.repos.MemberProviderRepo;
import com.deltadental.pcp.calculation.service.PCPAssignmentService;
import com.deltadental.pcp.calculation.util.MemberClaimUtils;
import com.deltadental.pcp.search.interservice.PCPSearchServiceClient;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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

    @Autowired
    private PCPSearchServiceClient pcpSearchService;

    @Autowired
    private MTVSyncServiceClient mtvSyncService;

    @Autowired
    private PCPConfigData pcpConfigData;

    @Autowired
    private MemberProviderRepo memberProviderRepo;

    @Autowired
    private MemberClaimServicesRepo memberClaimServicesRepo;

    @Autowired
    private MemberClaimRepo memberClaimRepo;

    @Autowired
    private ContractMemberClaimRepo contractMemberClaimRepo;

    @Autowired
    private PCPAssignmentService pcpAssignmentService;
    
    @Autowired
	private MemberClaimUtils memberClaimUtills;

    private List<ContractMemberClaimEntity> contractMemberClaimEntities;

	@MethodExecutionTime
	public void validateAndAssignProvider() {
		log.info("START PCPAssignmentTask.processPCPAssignment.");
		log.info("Processing {} ", contractMemberClaimEntities);
		StringBuilder errorMessageBuilder = new StringBuilder();
		List<String> memberClaimIds = memberClaimUtills.getClaimIds(contractMemberClaimEntities);
		try {
		List<MemberClaimResponse> memberClaimsResponses = mtvSyncService.memberClaim(memberClaimIds);
		if(CollectionUtils.isEmpty(memberClaimsResponses)) {
			errorMessageBuilder.append(String.format("Claim information not found for claim # %s", memberClaimIds.toString()));
			log.info("Marking as {} for Claim id {} with member claim response is null ",Status.CLAIM_NOT_FOUND, memberClaimIds.toString());
			setErrorMessageToAllContractAndSave(errorMessageBuilder, Status.CLAIM_NOT_FOUND);
		}else{
			log.info("Total Claims Recieved from MTV sync {} out of {}", memberClaimsResponses.size(), memberClaimIds.size());
			Multimap<String, MemberClaimResponse> memberWiseResponseMultiMap = ArrayListMultimap.create();
		    for(MemberClaimResponse memberClaimResponse : memberClaimsResponses) {
			  if (null != memberClaimResponse && (StringUtils.isBlank(memberClaimResponse.getErrorCode()) || StringUtils.isBlank(memberClaimResponse.getErrorMessage()))) {
				boolean exclusionFlag = pcpConfigData.isProviderInExclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber());
				boolean inclusionFlag = pcpConfigData.isProviderInInclusionList(memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber());
				if (exclusionFlag || inclusionFlag) {
					List<ServiceLine> serviceLines = memberClaimResponse.getServiceLines();
					if (CollectionUtils.isNotEmpty(serviceLines)) {
						boolean isExplanationCodeValid = pcpConfigData.isExplanationCodeValid(serviceLines);
						boolean isProcedureCodeValid = pcpConfigData.isProcedureCodeValid(serviceLines);
						boolean isClaimStatusValid = pcpConfigData.isClaimStatusValid(StringUtils.trimToNull(memberClaimResponse.getClaimStatus()));
						log.info("Claim id {} , isClaimStatusValid {}, isProcedureCodeValid {} and isExplanationCodeValid {} ",memberClaimResponse.getClaimId(),isClaimStatusValid,isProcedureCodeValid,isExplanationCodeValid);
						if (isClaimStatusValid && isExplanationCodeValid && isProcedureCodeValid) {
							memberWiseResponseMultiMap.put(memberClaimResponse.getMemberID(), memberClaimResponse);
						} else {
							if (!isClaimStatusValid) {
								appendColon(errorMessageBuilder);
								errorMessageBuilder.append(String.format("Claim status %s is not valid for PCP assignment!", StringUtils.trimToNull(memberClaimResponse.getClaimStatus())));
							}
							if (!isExplanationCodeValid) {
								appendColon(errorMessageBuilder);
								errorMessageBuilder.append("One of the Service Line Explanation Code[s] is not valid for this claim.");
							}
							if (!isProcedureCodeValid) {
								appendColon(errorMessageBuilder);
								errorMessageBuilder.append(", One of the Service Line Procedure Code[s] is not valid for this claim.");
							}
							log.info("PCP Assignment status for claim id {} is {}.", memberClaimResponse.getClaimId(), errorMessageBuilder);
							setErrorMessageAndSave(memberClaimResponse.getClaimId(), errorMessageBuilder,Status.RETRY);
						}
					} else {
						errorMessageBuilder.append(String.format("Service Line Items are empty for claim# %s ", memberClaimResponse.getClaimId()));
						setErrorMessageAndSave(memberClaimResponse.getClaimId(), errorMessageBuilder,Status.RETRY);
					}
				} else {
					if (!exclusionFlag) {
						errorMessageBuilder.append(String.format("Provider %s, Group %s, Division %s is listed in exlusion list.", memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()));
						setErrorMessageAndSave(memberClaimResponse.getClaimId(), errorMessageBuilder, Status.PCP_EXCLUDED);
					}
					if (!inclusionFlag) {
						appendColon(errorMessageBuilder);
						errorMessageBuilder.append(String.format("Provider %s, Group %s, Division %s is not listed in inclusion list.", memberClaimResponse.getProviderId(), memberClaimResponse.getGroupNumber(), memberClaimResponse.getDivisionNumber()));
						setErrorMessageAndSave(memberClaimResponse.getClaimId(), errorMessageBuilder, Status.PCP_NOT_INCLUDED);
					}
				}
			}
 		}
	    if(!memberWiseResponseMultiMap.isEmpty()) {
			pcpAssignmentService(contractMemberClaimEntities, memberWiseResponseMultiMap);
		}
		}
		
		} catch (Exception e) {
			log.error("Exception occured during retriving member claim information from Metavance Sync Service.", e);
			errorMessageBuilder.append("Exception occured during retriving member claim information from Metavance Sync Service.");
			setErrorMessageToAllContractAndSave(errorMessageBuilder,Status.RETRY);
		}		
		log.info("END PCPAssignmentTask.processPCPAssignment");
	}
	
    private void appendColon(StringBuilder strBuilder) {
        if (strBuilder != null && strBuilder.length() > 0) {
            strBuilder.append(": ");
        }
    }
    
    private void pcpAssignmentService(List<ContractMemberClaimEntity> contractMemberClaimEntities,
			Multimap<String, MemberClaimResponse> memberWiseResponseMap) {
    	    log.info("Start PCPAssignmentTask.pcpAssignmentService");
 		    contractMemberClaimEntities.forEach(contractMemberClaim -> {
			List<MemberClaimResponse> members = (List<MemberClaimResponse>) memberWiseResponseMap.get(contractMemberClaim.getMemberId());
			MemberClaimResponse memberClaimResponse = memberClaimUtills.calculateLatestClaim(members);
			pcpAssignmentService.process(contractMemberClaim, memberClaimResponse);
			log.info("END PCPAssignmentTask.pcpAssignmentService");
		});
    }	    
 	
	private void setErrorMessageAndSave(String claimId, StringBuilder errorMessageBuilder, Status status) {
		Optional<ContractMemberClaimEntity> contractMemberClaimEntity = 
				                                    findContractMemberClaimEntity(claimId);
			if(contractMemberClaimEntity.isPresent()) {
				ContractMemberClaimEntity entity = contractMemberClaimEntity.get();
				entity.setStatus(status);
				entity.setErrorMessage(errorMessageBuilder.toString());
				contractMemberClaimRepo.save(entity);
			}
	}
	
	private void setErrorMessageToAllContractAndSave(StringBuilder errorMessageBuilder, Status status) {
		contractMemberClaimEntities.forEach(entity ->{
				entity.setStatus(status);
				entity.setErrorMessage(errorMessageBuilder.toString());
				contractMemberClaimRepo.save(entity);
				contractMemberClaimRepo.save(entity);
			});			
	}   
	
	private Optional<ContractMemberClaimEntity> findContractMemberClaimEntity(String claimId) {
		return contractMemberClaimEntities.stream().filter(i -> i.getClaimId().equals(claimId)).findFirst();
	}
	
	public boolean checkMemberResponseForClaim(List<String> claimIds, String responseClaimId) {
		return claimIds.stream().anyMatch(i -> i.contains(responseClaimId));
	}

    @Override
    public void run() {
        validateAndAssignProvider();
    }
  }
