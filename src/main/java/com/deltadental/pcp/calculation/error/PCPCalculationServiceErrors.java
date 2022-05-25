package com.deltadental.pcp.calculation.error;

import org.springframework.http.HttpStatus;

import com.deltadental.platform.common.exception.ServiceException;

import lombok.Getter;

@Getter
public enum PCPCalculationServiceErrors {

	MTV_SYNC_CLAIM_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
	PROVIDER_ASSIGNMENT_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
	PCP_VALIDATE_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
	PCP_CONFIG_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_TIMESTAMP_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR), 
	NOT_FOUND(HttpStatus.NOT_FOUND),
	BAD_REQUEST(HttpStatus.BAD_REQUEST), 
	FORBIDDEN(HttpStatus.FORBIDDEN), 
	PROVIDER_NOT_VALIDATED(HttpStatus.NOT_FOUND), 
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED);
	
	private final HttpStatus statusCode;

	PCPCalculationServiceErrors(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}

	public ServiceException createException(Object... objects) {
		return new ServiceException(this.name(), this.statusCode, objects);
	}

	public HttpStatus httpStatus() {
		return this.statusCode;
	}
}
