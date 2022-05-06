package com.deltadental.pcp.calculation.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.deltadental.pcp.calculation.service.PCPValidatorService;

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
	private PCPValidatorService pcpValidatorService;

	@Scheduled(cron = "* */30 * * * *", zone = "America/Los_Angeles")
	// FIXME: move to properties
	@Synchronized
	public void process() {
		log.info("START PCPValidateScheduler.process()");
		pcpValidatorService.validatePending();
		log.info("END PCPValidateScheduler.process()");
	}
}
