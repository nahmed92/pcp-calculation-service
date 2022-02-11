package com.deltadental.pcp.calculation.repos;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deltadental.pcp.calculation.entities.MemberClaimServiceEntity;

@Repository
@Transactional
public interface MemberClaimServiceRepo extends JpaRepository<MemberClaimServiceEntity, Integer> {

	MemberClaimServiceEntity findByContractIdAndMemberIdAndProviderIdAndClaimId(String contractId, String memberId, String providerId, String claimId);

}
