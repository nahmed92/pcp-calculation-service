package com.deltadental.pcp.calculation.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.deltadental.pcp.calculation.entities.ContractMemberClaimsEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class PCPCalculationServiceRunnable implements Runnable {

	private ContractMemberClaimsEntity contractMemberClaimsEntity;

	@Autowired
	PCPAssignmentTask pcpAssignmentTask;
	
	@Override
	public void run() {
		String threadName = Thread.currentThread().getName();
		log.info("Thread # {} is started this task", threadName);
		pcpAssignmentTask.processPCPAssignment(this.contractMemberClaimsEntity);
		log.info("Thread # {} is finished the task.", threadName);
	}
}