package com.deltadental.pcp.calculation.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deltadental.pcp.calculation.entities.MemberClaimServiceEntity;

public interface MemberClaimServiceRepo extends JpaRepository<MemberClaimServiceEntity, Integer> {

	MemberClaimServiceEntity findByContractIdAndMemberIdAndProviderIdAndClaimId(String contractId, String memberId, String providerId, String claimId);

}
