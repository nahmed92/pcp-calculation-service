package com.deltadental.pcp.calculation.mapper;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimPK;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class Mapper {

	@MethodExecutionTime
	public List<ContractMemberClaimEntity> map(List<MemberContractClaimRequest> requests, String serviceInstanceId) {
		List<ContractMemberClaimEntity> response = new ArrayList<>();
		String id = UUID.randomUUID().toString();
		int sequence = 1;
		for (MemberContractClaimRequest request : requests) {
			if(StringUtils.startsWithIgnoreCase(request.getProviderId(), "DC")) {
				ContractMemberClaimEntity entity = map(request, serviceInstanceId);
				ContractMemberClaimPK pk = ContractMemberClaimPK.builder().id(id).sequenceId( sequence++).build();
				entity.setContractMemberClaimPK(pk);
				response.add(entity);
			}
		}
		return response;

	}

	private ContractMemberClaimEntity map(MemberContractClaimRequest request, String serviceInstanceId) {
		return ContractMemberClaimEntity.builder()
				.claimId(StringUtils.trimToNull(request.getClaimId()))
				.contractId(StringUtils.trimToNull(request.getContractId()))
				.memberId(StringUtils.trimToNull(request.getMemberId()))
				.providerId(StringUtils.trimToNull(request.getProviderId()))
				.state(StringUtils.trimToNull(request.getState()))
				.operatorId(StringUtils.trimToNull(request.getOperatorId()))
				.instanceId(StringUtils.trimToNull(serviceInstanceId)).status(Status.STAGED).build();
	}
}
