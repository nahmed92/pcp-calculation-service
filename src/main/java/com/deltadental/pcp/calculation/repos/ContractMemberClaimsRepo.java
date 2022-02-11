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
	List<ContractMemberClaimsEntity> findByStatus(String status);

	ContractMemberClaimsEntity findByClaimId(String claimId);
	
	@Modifying
	@Query("update ContractMemberClaimsEntity cmc set cmc.status = :status WHERE cmc.id = :id")
	void setStatus(@Param("id") Integer id, @Param("status") String status);
}
