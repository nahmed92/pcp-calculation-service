package com.deltadental.pcp.calculation.repos;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deltadental.pcp.calculation.entities.ProviderValidateEntity;

@Repository
@Transactional
public interface ProviderValidateRepo extends JpaRepository<ProviderValidateEntity, Integer> {

}
