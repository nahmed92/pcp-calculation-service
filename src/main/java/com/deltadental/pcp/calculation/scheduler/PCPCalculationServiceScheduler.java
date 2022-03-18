package com.deltadental.pcp.calculation.scheduler;

import java.util.Calendar;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PCPCalculationServiceScheduler {

//	@Scheduled(initialDelay = 1000, fixedRate = 10000)
	public void schedulePCPAssignments() {
		log.info("Current time is :: " + Calendar.getInstance().getTime());
	}
}
