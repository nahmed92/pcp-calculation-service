package com.deltadental.pcp.calculation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.deltadental.pcp.calculation.constants.PCPCalculationServiceConstants;
import com.deltadental.pcp.calculation.domain.MessageResponse;
import com.deltadental.pcp.calculation.domain.ValidateProviderRequest;
import com.deltadental.pcp.calculation.domain.ValidateProviderResponse;
import com.deltadental.pcp.calculation.service.ExcelHelper;
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
@RequestMapping(value = "/pcp-calculation")
@Api(value = "/pcp-calculation")
@Slf4j
@Data
@NoArgsConstructor
public class PCPCalculationServiceController {

	@Autowired
	PCPCalculationService pcpCalculationService;

	@ApiOperation(
			value = PCPCalculationServiceConstants.SUMMARY_ASSIGN_MEMBER_PCP, 
			notes = PCPCalculationServiceConstants.SUMMARY_ASSIGN_MEMBER_PCP_NOTES, 
			response = PCPAssignmentResponse.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Successfully assigned primary care provider for member.", response = ValidateProviderResponse.class),
                    @ApiResponse(code = 400, message = "Bad request.", response = ServiceError.class),
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
			value = PCPCalculationServiceConstants.SUMMARY_ASSIGN_PCPS_TO_MEMBERS, 
			notes = PCPCalculationServiceConstants.SUMMARY_ASSIGN_PCPS_TO_MEMBERS_NOTES, 
			response = PCPAssignmentResponse.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Successfully assigned primary care providers to members.", response = MessageResponse.class),
                    @ApiResponse(code = 400, message = "Bad request.", response = ServiceError.class),
                    @ApiResponse(code = 404, message = "Unable assign primary care provider for member.", response = ServiceError.class),
                    @ApiResponse(code = 500, message = "Internal server error.", response = ServiceError.class) })
	@ResponseBody
	@MethodExecutionTime
    @PostMapping(value = PCPCalculationServiceConstants.ASSIGN_PCPS_TO_MEMBERS_URI, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<MessageResponse> assignPCPsToMembers(@RequestBody List<ValidateProviderRequest> validateProviderRequestList) {
		log.info("START PCPCalculationServiceController.assignMemberPCP");
		if(!validateProviderRequestList.isEmpty()) {
			validateProviderRequestList.forEach(validateProviderRequest -> pcpCalculationService.saveContractMemberClaims(validateProviderRequest));
		}
		pcpCalculationService.assignPCPsToMembers();
		MessageResponse messageResponse = MessageResponse.builder().message("Successfully assigned primary care providers to members.").build();
		ResponseEntity<MessageResponse> responseEntity = new ResponseEntity<>(messageResponse, HttpStatus.OK); 
		log.info("END PCPCalculationServiceController.assignMemberPCP");
		return responseEntity;
	}
	
	@ApiOperation(
			value = PCPCalculationServiceConstants.PROCESS_PCP_MEMBER_CONTRACT, 
			notes = PCPCalculationServiceConstants.PROCESS_PCP_MEMBER_CONTRACT_NOTES, 
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
	
	@ApiOperation(
			value = PCPCalculationServiceConstants.UPLOAD_PCP_MEMBER_CLAIMS, 
			notes = PCPCalculationServiceConstants.UPLOAD_PCP_MEMBER_CLAIMS_NOTES, 
			response = String.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Successfully uploaded pcp member claims.", response = String.class),
                    @ApiResponse(code = 400, message = "Bad request.", response = ServiceError.class),
                    @ApiResponse(code = 404, message = "Unable to upload pcp member claims.", response = ServiceError.class),
                    @ApiResponse(code = 500, message = "Internal server error.", response = ServiceError.class) })
	@ResponseBody
	@MethodExecutionTime
    @PostMapping(value = PCPCalculationServiceConstants.UPLOAD_PCP_MEMBER_CLAIMS_URI, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<MessageResponse> uploadPCPMemberClaims(@RequestParam("pcpMemberClaimsDataFile") MultipartFile pcpMemberClaimsDataFile) {
		log.info("START PCPCalculationServiceController.uploadPCPMemberClaims");
		MessageResponse messageResponse = MessageResponse.builder().build();
		if(ExcelHelper.hasExcelFormat(pcpMemberClaimsDataFile)) {
			List<ValidateProviderRequest> validateProviderRequests = ExcelHelper.extractPCPMemberClaimsData(pcpMemberClaimsDataFile);
			if(!validateProviderRequests.isEmpty()) {
				validateProviderRequests.forEach(validateProviderRequest -> pcpCalculationService.saveContractMemberClaims(validateProviderRequest));
			}
			pcpCalculationService.assignPCPsToMembers();
			log.info("Valid excel uploaded!");
		} else {
			log.info("Invalid excel uploaded!");
			messageResponse.setMessage("Successfully assigned primary care providers to members.");
		}
		messageResponse.setMessage("Successfully assigned primary care providers to members.");
		ResponseEntity<MessageResponse> responseEntity = new ResponseEntity<>(messageResponse, HttpStatus.OK); 
		log.info("END PCPCalculationServiceController.uploadPCPMemberClaims");
		return responseEntity;
	}
}
