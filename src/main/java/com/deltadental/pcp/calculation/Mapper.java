package com.deltadental.pcp.calculation;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;
import com.deltadental.pcp.calculation.enums.STATUS;

@Component
public class Mapper {

	public ContractMemberClaimsEntity map(MemberContractClaimRequest memberContractClaimRequest,String serviceInstanceId) {
		ContractMemberClaimsEntity contractMemberClaimsEntity = ContractMemberClaimsEntity.builder()
				.claimId(StringUtils.trimToNull(memberContractClaimRequest.getClaimId()))
				.contractId(StringUtils.trimToNull(memberContractClaimRequest.getContractId()))
				.memberId(StringUtils.trimToNull(memberContractClaimRequest.getMemberId()))
				.providerId(StringUtils.trimToNull(memberContractClaimRequest.getProviderId()))
				.state(StringUtils.trimToNull(memberContractClaimRequest.getState()))
				.operatorId(StringUtils.trimToNull(memberContractClaimRequest.getOperatorId()))
				.instanceId(StringUtils.trimToNull(serviceInstanceId)).status(STATUS.STAGED.name()).build();
		return contractMemberClaimsEntity;
	}

}
