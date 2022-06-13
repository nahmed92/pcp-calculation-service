package com.deltadental.pcp.calculation.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.deltadental.pcp.calculation.entities.ContractMemberClaimEntity;
import com.deltadental.pcp.calculation.entities.ContractMemberClaimPK;
import com.deltadental.pcp.calculation.enums.Status;

@Repository
@Transactional(readOnly = false)
public interface ContractMemberClaimRepo extends JpaRepository<ContractMemberClaimEntity, ContractMemberClaimPK> {

	// FIXME: remove query
	@Query("SELECT cmc FROM ContractMemberClaimEntity cmc WHERE  cmc.claimId = :claimId AND  cmc.contractId = :contractId AND  cmc.memberId = :memberId AND cmc.providerId = :providerId AND cmc.state = :state AND cmc.status in :status")
	List<ContractMemberClaimEntity> findByClaimIdAndContractIdAndMemberIdAndProviderIdAndStateAndStatusInList(
			@Param("claimId") String claimId, @Param("contractId") String contractId,
			@Param("memberId") String memberId,  @Param("providerId") String providerId, @Param("state") String state,
			@Param("status") List<Status> status);

	@Query("SELECT cmc FROM ContractMemberClaimEntity cmc WHERE cmc.instanceId = :instanceId AND cmc.status in :status")
	List<ContractMemberClaimEntity> findByInstanceIdWhereStatusInList(@Param("instanceId") String instanceId,
			@Param("status") List<Status> status);

}
