package com.deltadental.pcp.calculation.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.deltadental.pcp.calculation.Mapper;
import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;
import com.deltadental.pcp.calculation.enums.STATUS;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimsRepo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MemberContractClaimService {

	@Autowired
	private ContractMemberClaimsRepo repo;

	@Autowired
	private Mapper mapper;

	@Value("${service.instance.id}")
	private String serviceInstanceId;

	private static final List<String> statusList = new ArrayList<>();

	static {
		statusList.add(STATUS.RETRY.getStatus());
		statusList.add(STATUS.STAGED.getStatus());
		statusList.add(STATUS.VALIDATED.getStatus());
		statusList.add(STATUS.PCP_ASSIGNED.getStatus());
	}

	private void save(MemberContractClaimRequest memberContractClaimRequest) {
		log.info("START MemberContractClaimService.save");
		List<ContractMemberClaimsEntity> memberClaimsEntities = repo
				.findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatusInList(
						StringUtils.trimToNull(memberContractClaimRequest.getClaimId()), // check this and remove
						StringUtils.trimToNull(memberContractClaimRequest.getContractId()),
						StringUtils.trimToNull(memberContractClaimRequest.getMemberId()),
						StringUtils.trimToNull(memberContractClaimRequest.getProviderId()),
						StringUtils.trimToNull(memberContractClaimRequest.getState()), statusList);

		if (CollectionUtils.isEmpty(memberClaimsEntities)) {
			log.info("Inserting  {} ", memberContractClaimRequest);
			ContractMemberClaimsEntity contractMemberClaimsEntity = mapper.map(memberContractClaimRequest,serviceInstanceId);
			
			repo.save(contractMemberClaimsEntity);

		} else {
			log.warn("Record already exists in contract member claims table : {} ", memberContractClaimRequest);
		}
		log.info("END MemberContractClaimService.save");
	}

	public void stageMemberContractClaimRecords(List<MemberContractClaimRequest> memberContractClaimRequests) {
		log.info("START MemberContractClaimService.stageMemberContractClaimRecords");
		if (CollectionUtils.isNotEmpty(memberContractClaimRequests)) {
			memberContractClaimRequests.forEach(i -> save(i));
		}
		log.info("END MemberContractClaimService.stageMemberContractClaimRecords");
	}

}
