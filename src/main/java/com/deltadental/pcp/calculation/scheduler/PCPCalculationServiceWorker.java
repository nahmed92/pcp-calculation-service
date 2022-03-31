package com.deltadental.pcp.calculation.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimsRepo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PCPCalculationServiceWorker {

	@Value("${pcp.assignment.process.workers.count:5}")
	private int pcpAssignmentProcessWorkersCount;

	@Value("${service.instance.id}")
	private String serviceInstanceId;

	private Executor executor;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ContractMemberClaimsRepo contractMemberClaimsRepo;

	@PostConstruct
	public void setup() {
		executor = Executors.newFixedThreadPool(pcpAssignmentProcessWorkersCount);
	}

	@Lookup
	PCPAssignmentTask createTask() {
		return null;
	}

	public void submitTask(ContractMemberClaimsEntity contractMemberClaimsEntity) {
		log.info("START PCPCalculationServiceWorker.submitTask()");
		if (null != contractMemberClaimsEntity) {
			PCPAssignmentTask pcpAssignmentTask = createTask();
			pcpAssignmentTask.setRestTemplate(restTemplate);
			pcpAssignmentTask.setContractMemberClaimsEntity(contractMemberClaimsEntity);
			executor.execute(pcpAssignmentTask);
		}
		log.info("END PCPCalculationServiceWorker.submitTask()");
	}

	public void processPCPAssignmentRequests() {
		log.info("START PCPCalculationServiceWorker.processPCPAssignmentRequests()");
		List<ContractMemberClaimsEntity> contractMemberClaimsEntities = contractMemberClaimsRepo.findByInstanceIdWhereStatusIsNull(serviceInstanceId);
		if (CollectionUtils.isNotEmpty(contractMemberClaimsEntities)) {
			log.info("Processing total {} pcp assignment requests on service instance {} ",
					contractMemberClaimsEntities.size(), serviceInstanceId);
			contractMemberClaimsEntities.forEach(contractMemberClaimEntity -> {
				try {
					log.info("Processing pcp assignment request for contract member claim {}", contractMemberClaimEntity.toString());
					submitTask(contractMemberClaimEntity);
				} catch (Exception e) {
					log.error("Exception processing pcp assingment request for contract member claim {} ", contractMemberClaimEntity.toString(), e);
				}
			});
		} else {
			log.info("No pending requests for pcp assignment.");
		}
		log.info("END PCPCalculationServiceWorker.processPCPAssignmentRequests()");
	}
}
