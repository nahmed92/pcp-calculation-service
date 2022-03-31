package com.deltadental.pcp.calculation.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PCPCalculationServiceScheduler {

	@Autowired
	private PCPCalculationServiceWorker worker;

//	@Scheduled(initialDelayString = "${pcp.assignment.job.initial.delay}", fixedDelayString = "${pcp.assignment.job.fixed.delay}")
//	@Synchronized
	public void processPendingPCPAssignmentRequest() {
		log.info("START PCPCalculationServiceScheduler.processPendingPCPAssignmentRequest()");
		worker.processPCPAssignmentRequests();
		log.info("END PCPCalculationServiceScheduler.processPendingPCPAssignmentRequest()");
	}
}
