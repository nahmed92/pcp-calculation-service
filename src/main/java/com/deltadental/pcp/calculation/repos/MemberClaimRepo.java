package com.deltadental.pcp.calculation.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.deltadental.pcp.calculation.entities.MemberClaimEntity;
@Transactional(readOnly = false)
public interface MemberClaimRepo extends JpaRepository<MemberClaimEntity, Integer> {

}
