package com.deltadental.pcp.calculation.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.deltadental.pcp.calculation.worker.PCPCalculationServiceWorker;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
@Data
public class PCPValidateScheduler {

	@Autowired
	private PCPCalculationServiceWorker worker;

	@Scheduled(initialDelayString = "${scheduling.job.pcp.validation.delay}", fixedDelayString = "${scheduling.job.pcp.validation.delay}")
	@MethodExecutionTime
    @Synchronized
    public void process() {
        log.info("START PCPValidateScheduler.process()");
        worker.processPCPAssignmentRequests();
        log.info("END PCPValidateScheduler.process()");
    }
}
