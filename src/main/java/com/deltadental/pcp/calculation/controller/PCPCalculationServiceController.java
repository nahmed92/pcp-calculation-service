package com.deltadental.pcp.calculation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.deltadental.pcp.calculation.constants.PCPCalculationServiceConstants;
import com.deltadental.pcp.calculation.domain.ValidateProviderResponse;
import com.deltadental.pcp.calculation.service.PCPCalculationService;
import com.deltadental.pcp.search.service.pojos.PCPAssignmentResponse;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.deltadental.platform.common.exception.ServiceError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/pcp-calculation")
@Api(value = "/pcp-calculation")
@Slf4j
@Data
@NoArgsConstructor
public class PCPCalculationServiceController {

	@Autowired
	PCPCalculationService pcpCalculationService;
	
	
	@ApiOperation(
			value = PCPCalculationServiceConstants.SUMMARY_VALIDATE_PROVIDER, 
			notes = PCPCalculationServiceConstants.SUMMARY_VALIDATE_PROVIDER_NOTES, 
			response = PCPAssignmentResponse.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Successfully validated provider.", response = ValidateProviderResponse.class),
                    @ApiResponse(code = 400, message = "Bad request.", response = ServiceError.class),
//                    @ApiResponse(code = 403, message = "Unauthorized", response = ServiceError.class),
                    @ApiResponse(code = 404, message = "Unable validate provider.", response = ServiceError.class),
                    @ApiResponse(code = 500, message = "Internal server error.", response = ServiceError.class) })
	@ResponseBody
	@MethodExecutionTime
    @PostMapping(value = PCPCalculationServiceConstants.VALIDATE_PROVIDER_URI, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<ValidateProviderResponse> validateProvider() {
		log.info("START PCPCalculationServiceController.validateProvider");
		ValidateProviderResponse validateProviderResponse = new ValidateProviderResponse(); 
		validateProviderResponse.setStatus(pcpCalculationService.validateProvider());
		ResponseEntity<ValidateProviderResponse> responseEntity = new ResponseEntity<>(validateProviderResponse, HttpStatus.OK); 
		log.info("END PCPCalculationServiceController.validateProvider");
		return responseEntity;
	}
}
