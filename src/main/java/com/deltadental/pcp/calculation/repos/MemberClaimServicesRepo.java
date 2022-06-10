package com.deltadental.pcp.calculation.repos;

import com.deltadental.pcp.calculation.entities.MemberClaimServicesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = false)
public interface MemberClaimServicesRepo extends JpaRepository<MemberClaimServicesEntity, Integer> {

}
