package com.deltadental.pcp.calculation.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deltadental.pcp.calculation.entities.MemberClaimEntity;

public interface MemberClaimRepo extends JpaRepository<MemberClaimEntity, Integer> {

}
