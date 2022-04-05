package com.deltadental.pcp.calculation.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.deltadental.pcp.calculation.entities.MemberProviderEntity;

@Repository
@Transactional(readOnly = false)
public interface MemberProviderRepo extends JpaRepository<MemberProviderEntity, Integer> {

	@Modifying
	@Query("update MemberProviderEntity mpe set mpe.status = :status, mpe.lastMaintTs = CURRENT_TIMESTAMP WHERE mpe.id = :id")
	void setStatus(@Param("id") Integer id, @Param("status") String status);
}
