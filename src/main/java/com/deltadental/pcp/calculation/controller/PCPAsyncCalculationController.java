package com.deltadental.pcp.calculation.controller;

import java.util.concurrent.Callable;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import com.deltadental.pcp.calculation.constants.PCPCalculationServiceConstants;
import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.domain.MemberContractClaimResponse;
import com.deltadental.pcp.calculation.domain.MessageResponse;
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

@CrossOrigin(exposedHeaders = "Content-Disposition")
@RestController
@RequestMapping(value = "/async/pcp-calculation")
@Api(value = "/async/pcp-calculation")
@Slf4j
@Data
@Validated
@NoArgsConstructor
public class PCPAsyncCalculationController {

	@Autowired
	PCPCalculationService pcpCalculationService;
	
	@ApiOperation(
			value = PCPCalculationServiceConstants.SUMMARY_MEMBER_CONTRACT_CLAIM, 
			notes = PCPCalculationServiceConstants.SUMMARY_MEMBER_CONTRACT_CLAIM_NOTES, 
			response = PCPAssignmentResponse.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Successfully staged data for pcp assignment..", response = MemberContractClaimResponse.class),
                    @ApiResponse(code = 400, message = "Bad request.", response = ServiceError.class),
                    @ApiResponse(code = 404, message = "Unable stage the data for pcp assignment..", response = ServiceError.class),
                    @ApiResponse(code = 500, message = "Internal server error.", response = ServiceError.class) })
	@ResponseBody
	@MethodExecutionTime
    @PostMapping(value = PCPCalculationServiceConstants.MEMBER_CONTRACT_CLAIM_URI, produces = {MediaType.APPLICATION_JSON_VALUE})
	public WebAsyncTask<MessageResponse> memberContractClaim(@Valid @RequestBody MemberContractClaimRequest validateProviderRequest) {
		log.info("START PCPAsyncCalculationController.memberContractClaim");
		Callable<MessageResponse> callable = new Callable<MessageResponse>() {
			@Override
			public MessageResponse call() throws Exception {
				pcpCalculationService.stageMemberContractClaimRecord(validateProviderRequest); 
				MessageResponse messageResponse = MessageResponse.builder().message("Successfully staged member contract request.").build();
				return messageResponse;
			}			
		};
		log.info("END PCPAsyncCalculationController.memberContractClaim");
		return new WebAsyncTask<MessageResponse>(callable);
	}
}
