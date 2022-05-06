package com.deltadental.pcp.calculation.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.deltadental.pcp.calculation.entities.MemberProviderEntity;

@Repository
@Transactional(readOnly = false)
public interface MemberProviderRepo extends JpaRepository<MemberProviderEntity, Integer> {

}
