package com.deltadental.pcp.calculation.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.deltadental.pcp.calculation.entities.PCPCalculationActivityEntity;

@Repository
@Transactional
public interface PCPCalculationServiceActivityRepo  extends JpaRepository<PCPCalculationActivityEntity, Integer> {

}
