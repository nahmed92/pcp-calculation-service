package com.deltadental.pcp.calculation.scheduler;

import com.deltadental.pcp.calculation.worker.PCPCalculationServiceWorker;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
@Data
public class PendingPCPAssignmentScheduler {

    @Autowired
    private PCPCalculationServiceWorker worker;

    @Scheduled(initialDelayString = "${scheduling.job.pcp.assignment.delay}", fixedDelayString = "${scheduling.job.pcp.assignment.delay}")
	@MethodExecutionTime
    @Synchronized
    public void process() {
        log.info("START PCPCalculationServiceScheduler.process()");
        worker.processPCPAssignmentRequests();
        log.info("END PCPCalculationServiceScheduler.process()");
    }
}
