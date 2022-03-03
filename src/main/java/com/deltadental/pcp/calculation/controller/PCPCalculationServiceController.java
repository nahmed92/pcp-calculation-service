package com.deltadental.pcp.calculation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.deltadental.pcp.calculation.constants.PCPCalculationServiceConstants;
import com.deltadental.pcp.calculation.domain.ValidateProviderRequest;
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
	public ResponseEntity<ValidateProviderResponse> validateProvider(@RequestBody ValidateProviderRequest validateProviderRequest) {
		log.info("START PCPCalculationServiceController.validateProvider");
		ValidateProviderResponse validateProviderResponse = pcpCalculationService.validateProvider(validateProviderRequest); 
		ResponseEntity<ValidateProviderResponse> responseEntity = new ResponseEntity<>(validateProviderResponse, HttpStatus.OK); 
		log.info("END PCPCalculationServiceController.validateProvider");
		return responseEntity;
	}
	
	@ApiOperation(
			value = PCPCalculationServiceConstants.SUMMARY_ASSIGN_MEMBER_PCP, 
			notes = PCPCalculationServiceConstants.SUMMARY_ASSIGN_MEMBER_PCP_NOTES, 
			response = PCPAssignmentResponse.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Successfully assigned primary care provider for member.", response = ValidateProviderResponse.class),
                    @ApiResponse(code = 400, message = "Bad request.", response = ServiceError.class),
//                    @ApiResponse(code = 403, message = "Unauthorized", response = ServiceError.class),
                    @ApiResponse(code = 404, message = "Unable assign primary care provider for member.", response = ServiceError.class),
                    @ApiResponse(code = 500, message = "Internal server error.", response = ServiceError.class) })
	@ResponseBody
	@MethodExecutionTime
    @PostMapping(value = PCPCalculationServiceConstants.ASSIGN_MEMBER_PCP_URI, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<ValidateProviderResponse> assignMemberPCP(@RequestBody ValidateProviderRequest validateProviderRequest) {
		log.info("START PCPCalculationServiceController.assignMemberPCP");
		ValidateProviderResponse validateProviderResponse = pcpCalculationService.assignMemberPCP(validateProviderRequest); 
		ResponseEntity<ValidateProviderResponse> responseEntity = new ResponseEntity<>(validateProviderResponse, HttpStatus.OK); 
		log.info("END PCPCalculationServiceController.assignMemberPCP");
		return responseEntity;
	}
	
	@ApiOperation(
			value = PCPCalculationServiceConstants.PROCESS_PCP_MEMBER_CONTRACT, 
			notes = PCPCalculationServiceConstants.SPROCESS_PCP_MEMBER_CONTRACT_NOTES, 
			response = Contract.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Successfully validated provider.", response = Contract.class),
                    @ApiResponse(code = 400, message = "Bad request.", response = ServiceError.class),
                    @ApiResponse(code = 403, message = "Unauthorized", response = ServiceError.class),
                    @ApiResponse(code = 404, message = "Contracts Processor not found.", response = ServiceError.class),
                    @ApiResponse(code = 500, message = "Internal server error.", response = ServiceError.class) })
	@ResponseBody
	@MethodExecutionTime
    @PostMapping(value = PCPCalculationServiceConstants.PROCESS_PCP_MEMBER_CONTRACT_URI, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<Contract>> processPcpMemberContract(@RequestBody final List<Contract> contracts) {
		log.info("START PCPCalculationServiceController.processPcpMemberContract");
		pcpCalculationService.setAssginmentDate(contracts);
		ResponseEntity<List<Contract>> responseEntity = new ResponseEntity<>(contracts, HttpStatus.OK); 
		log.info("END PCPCalculationServiceController.processPcpMemberContract");
		return responseEntity;
	}
}
