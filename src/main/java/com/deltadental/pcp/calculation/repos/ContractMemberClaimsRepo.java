package com.deltadental.pcp.calculation.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;

@Repository
@Transactional(readOnly = false)
public interface ContractMemberClaimsRepo extends JpaRepository<ContractMemberClaimsEntity, Integer> {

	@Query("SELECT cmc FROM ContractMemberClaimsEntity cmc WHERE  cmc.claimId = :claimId AND  cmc.contractId = :contractId AND  cmc.memberId = :memberId AND  cmc.providerId = :providerId AND cmc.state = :state AND cmc.status in :statusList")
	List<ContractMemberClaimsEntity> findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatusInList(@Param("claimId") String claimId, 
			@Param("contractId") String contractId, 
			@Param("memberId") String memberId, 
			@Param("providerId") String providerId,
			@Param("state") String state,
			@Param("statusList") List<String> statusList);

	@Query("SELECT cmc FROM ContractMemberClaimsEntity cmc WHERE cmc.instanceId = :instanceId AND cmc.status in :statusList")
	List<ContractMemberClaimsEntity> findByInstanceIdWhereStatusInList(@Param("instanceId") String instanceId, @Param("statusList") List<String> statusList);
	
	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update ContractMemberClaimsEntity cmc set cmc.status = :status, cmc.lastMaintTs = CURRENT_TIMESTAMP WHERE cmc.contractMemberClaimId = :contractMemberClaimId")
	void setStatus(@Param("contractMemberClaimId") Integer contractMemberClaimId, @Param("status") String status);
	
}
