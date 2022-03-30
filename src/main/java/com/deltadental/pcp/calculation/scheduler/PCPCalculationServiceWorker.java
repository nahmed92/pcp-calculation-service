package com.deltadental.pcp.calculation.scheduler;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;
import com.deltadental.pcp.calculation.repos.ContractMemberClaimsRepo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PCPCalculationServiceWorker {

	@Value("${pcp.assignment.process.workers.count:5}")
	private int pcpAssignmentProcessWorkersCount;

	private Executor executor;

	@Autowired
	private ContractMemberClaimsRepo contractMemberClaimsRepo;

	@PostConstruct
	public void setup() {
		executor = Executors.newFixedThreadPool(pcpAssignmentProcessWorkersCount);
	}

	@Lookup
	PCPCalculationServiceRunnable createTask() {
		return null;
	}

	public void submitTask(ContractMemberClaimsEntity contractMemberClaimsEntity) {
		log.info("START PCPCalculationServiceWorker.submitTask()");
		if(null != contractMemberClaimsEntity) {
			PCPCalculationServiceRunnable calculationServiceRunnable = createTask();
			calculationServiceRunnable.setContractMemberClaimsEntity(contractMemberClaimsEntity);
			executor.execute(calculationServiceRunnable);
		}
		log.info("END PCPCalculationServiceWorker.submitTask()");
	}

	public void processPCPAssignmentRequests() {
		log.info("START PCPCalculationServiceWorker.processPCPAssignmentRequests()");
		List<String> distinctStates = contractMemberClaimsRepo.findDistinctStateWhereStatusIsNull();
		if (CollectionUtils.isNotEmpty(distinctStates)) {
			log.info("Processing total {} pcp assignment requests ", // in serviceInstance {} ",
					distinctStates.size()); // ,
			// distinctStates.getServiceInstanceId());
			distinctStates.forEach(state -> {
				try {
					log.info("Processing pcp assignment request for state {}", state);
					processPCPAssignmentRequestForState(state);
				} catch (Exception e) {
					log.error("Exception processing pcp assingment request for state {} ", state, e);
				}
			});
		} else {
			log.info("No pending requests for pcp assignment.");
		}
		log.info("END PCPCalculationServiceWorker.processPCPAssignmentRequests()");
	}

	private void processPCPAssignmentRequestForState(String state) {
		log.info("START PCPCalculationServiceWorker.processPCPAssignmentRequestForState()");
		List<ContractMemberClaimsEntity> contractMemberClaimsEntities = contractMemberClaimsRepo.findByStateAndStatus(state, null);
		if (CollectionUtils.isNotEmpty(contractMemberClaimsEntities)) {
			contractMemberClaimsEntities.parallelStream().forEach(contractMemberClaimEntity -> {
				submitTask(contractMemberClaimEntity);
			});
		} else {
			log.info("No pending pcp assignment requests for state {}", state);
		}
		log.info("END PCPCalculationServiceWorker.processPCPAssignmentRequestForState()");
	}
}
