package com.deltadental.pcp.calculation.service;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.pcp.calculation.mapper.Mapper;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimRepo;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MemberContractClaimService {

  @Autowired
	private ContractMemberClaimRepo repo;

	@Autowired
	private PCPValidatorService pcpValidatorService;

	@Autowired
	private Mapper mapper;

	@Value("${service.instance.id}")
	private String serviceInstanceId;

	private static final List<Status> SEARCH_STATUS = List.of(Status.RETRY, Status.STAGED, Status.VALIDATED,
			Status.PCP_ASSIGNED, Status.PCP_EXCLUDED, Status.PCP_NOT_INCLUDED, Status.PCP_ALREADY_ASSIGNED);

  @Transactional
  @MethodExecutionTime
	private List<ContractMemberClaimEntity> save(List<MemberContractClaimRequest> requests) {
		log.info("START MemberContractClaimService.save");

		List<ContractMemberClaimEntity> entities = List.of();

		if (CollectionUtils.isNotEmpty(requests)) {
				// FIXME:
				List<MemberContractClaimRequest>  memberClaimRequests= getLatestMemberClaimsEntities(requests);
				if (CollectionUtils.isEmpty(memberClaimRequests)) {
					log.info("Inserting  {} ", memberClaimRequests);
					entities = mapper.map(memberClaimRequests, serviceInstanceId);
					// stage member contract
					repo.saveAll(entities);
				} 
		}
		log.info("END MemberContractClaimService.save");
		return entities;
	}
	
	private List<MemberContractClaimRequest> getLatestMemberClaimsEntities(List<MemberContractClaimRequest> requests) {
		List<MemberContractClaimRequest> memberClaimsRequests = new ArrayList<>();
		for(MemberContractClaimRequest request: requests) {
			// FIXME:
			List<ContractMemberClaimEntity> memberClaimsEntities = repo
					.findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatusInList(
							StringUtils.trimToNull(request.getClaimId()), // check this and remove
							StringUtils.trimToNull(request.getContractId()),
							StringUtils.trimToNull(request.getProviderId()),
							StringUtils.trimToNull(request.getMemberId()),
							StringUtils.trimToNull(request.getState()), SEARCH_STATUS);
			if(CollectionUtils.isEmpty(memberClaimsEntities)) {
				memberClaimsRequests.add(request);
			} else {
				log.warn("Record already exists in contract member claims table : {} ", request);
			}
		
		}
			return memberClaimsRequests;
	}

  @MethodExecutionTime
	public void stageMemberContractClaimRecords(List<MemberContractClaimRequest> memberContractClaimRequests) {
		log.info("START MemberContractClaimService.stageMemberContractClaimRecords");
		List<ContractMemberClaimEntity> entities = save(memberContractClaimRequests);
		if(CollectionUtils.isNotEmpty(entities)) {
		pcpValidatorService.validateAndAssignPCP(entities);
	}
		log.info("END MemberContractClaimService.stageMemberContractClaimRecords");
	}
}
