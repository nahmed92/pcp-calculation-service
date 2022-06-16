package com.deltadental.pcp.calculation.mapper;

import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.enums.Status;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Mapper {

    @MethodExecutionTime
    public ContractMemberClaimEntity map(MemberContractClaimRequest request, String serviceInstanceId) {
        ContractMemberClaimEntity contractMemberClaimsEntity = ContractMemberClaimEntity.builder()
                .id(UUID.randomUUID().toString())
                .claimId(StringUtils.trimToNull(request.getClaimId()))
                .contractId(StringUtils.trimToNull(request.getContractId()))
                .memberId(StringUtils.trimToNull(request.getMemberId()))
                .providerId(StringUtils.trimToNull(request.getProviderId()))
                .state(StringUtils.trimToNull(request.getState()))
                .operatorId(StringUtils.trimToNull(request.getOperatorId()))
                .instanceId(StringUtils.trimToNull(serviceInstanceId)).status(Status.STAGED).build();
        return contractMemberClaimsEntity;
    }

}
