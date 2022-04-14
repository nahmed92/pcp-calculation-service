package com.deltadental.pcp.calculation.controller;

import lombok.Getter;

/**
 * PCP CALCULATION SERVICE STATUS
 */
@Getter
public enum STATUS {
	STAGED("STAGED"),
	VALIDATED("VALIDATED"),
//	VALIDATION_FAILED(),
	ERROR("ERROR"),
//	PROCESSED,
	PCP_ASSIGNED("PCP ASSIGNED"),
	RETRY("RE-TRY");

	private final String status;
	
	STATUS(String status) {
		this.status = status;
	}
}
