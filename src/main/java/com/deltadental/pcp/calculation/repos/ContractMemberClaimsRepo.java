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

	ContractMemberClaimsEntity findByClaimId(String claimId);
	
	ContractMemberClaimsEntity findByClaimIdAndStatus(String claimId, String status);
	
	List<ContractMemberClaimsEntity> findByClaimIdAndContractIdAndMemberIdAndProviderId(String claimId, String contrctId, String memberId, String providerId);
	
	@Query("SELECT cmc FROM ContractMemberClaimsEntity cmc WHERE  cmc.claimId = :claimId AND  cmc.contractId = :contractId AND  cmc.memberId = :memberId AND  cmc.providerId = :providerId AND cmc.state = :state AND (cmc.status IS NULL OR cmc.status = :status)")
	List<ContractMemberClaimsEntity> findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatusIsNullOrValue(@Param("claimId") String claimId, 
			@Param("contractId") String contractId, 
			@Param("memberId") String memberId, 
			@Param("providerId") String providerId,
			@Param("state") String state,
			@Param("status") String status);

	@Query("SELECT cmc FROM ContractMemberClaimsEntity cmc WHERE  cmc.claimId = :claimId AND  cmc.contractId = :contractId AND  cmc.memberId = :memberId AND  cmc.providerId = :providerId AND cmc.state = :state AND cmc.status = :status")
	List<ContractMemberClaimsEntity> findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatus(@Param("claimId") String claimId, 
			@Param("contractId") String contractId, 
			@Param("memberId") String memberId, 
			@Param("providerId") String providerId,
			@Param("state") String state,
			@Param("status") String status);
	
	List<ContractMemberClaimsEntity> findByStatus(String status);
	
	List<ContractMemberClaimsEntity> findByStateAndStatus(String state, String status);

	@Query("SELECT DISTINCT cmc.state FROM ContractMemberClaimsEntity cmc WHERE cmc.status is null")
	List<String> findDistinctStateWhereStatusIsNull();
	
	@Query("SELECT cmc FROM ContractMemberClaimsEntity cmc WHERE cmc.instanceId = :instanceId AND cmc.status in :statusList")
	List<ContractMemberClaimsEntity> findByInstanceIdWhereStatusIsNull(@Param("instanceId") String instanceId, List<String> statusList);
	
	@Query("SELECT cmc FROM ContractMemberClaimsEntity cmc WHERE cmc.instanceId = :instanceId AND cmc.status = :status")
	List<ContractMemberClaimsEntity> findByInstanceIdAndStatus(@Param("instanceId") String instanceId, @Param("status") String status);
	
	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update ContractMemberClaimsEntity cmc set cmc.status = :status, cmc.lastMaintTs = CURRENT_TIMESTAMP WHERE cmc.contractMemberClaimId = :contractMemberClaimId")
	void setStatus(@Param("contractMemberClaimId") Integer contractMemberClaimId, @Param("status") String status);
	
}
