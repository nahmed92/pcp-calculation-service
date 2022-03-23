package com.deltadental.pcp.calculation.repos;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;

@Repository
@Transactional
public interface ContractMemberClaimsRepo extends JpaRepository<ContractMemberClaimsEntity, Integer> {

	ContractMemberClaimsEntity findByClaimId(String claimId);
	
	ContractMemberClaimsEntity findByClaimIdAndStatus(String claimId, String status);
	
	List<ContractMemberClaimsEntity> findByClaimIdAndContractIdAndMemberIdAndProviderId(String claimId, String contrctId, String memberId, String providerId);
	
	@Query("SELECT cmc FROM ContractMemberClaimsEntity cmc WHERE  cmc.claimId = :claimId AND  cmc.contractId = :contractId AND  cmc.memberId = :memberId AND  cmc.providerId = :providerId AND cmc.status IS NULL")
	List<ContractMemberClaimsEntity> findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStatusIsNull(@Param("claimId") String claimId, 
			@Param("contractId") String contractId, 
			@Param("memberId") String memberId, 
			@Param("providerId") String providerId);

	@Query("SELECT cmc FROM ContractMemberClaimsEntity cmc WHERE  cmc.claimId = :claimId AND  cmc.contractId = :contractId AND  cmc.memberId = :memberId AND  cmc.providerId = :providerId AND cmc.status = :status")
	List<ContractMemberClaimsEntity> findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStatus(@Param("claimId") String claimId, 
			@Param("contractId") String contractId, 
			@Param("memberId") String memberId, 
			@Param("providerId") String providerId,
			@Param("status") String status);
	
	List<ContractMemberClaimsEntity> findByStatus(String status);
	
	List<ContractMemberClaimsEntity> findByStateAndStatus(String state, String status);

	@Query("SELECT DISTINCT cmc.state FROM ContractMemberClaimsEntity cmc WHERE cmc.status is null")
	List<String> findDistinctStateWhereStatusIsNull();
	
	@Modifying
	@Query("update ContractMemberClaimsEntity cmc set cmc.status = :status WHERE cmc.contractMemberClaimId = :contractMemberClaimId")
	void setStatus(@Param("contractMemberClaimId") Integer contractMemberClaimId, @Param("status") String status);
	
}
