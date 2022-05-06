package com.deltadental.pcp.calculation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.deltadental.pcp.calculation.constants.PCPCalculationServiceConstants;
import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.domain.MessageResponse;
import com.deltadental.pcp.calculation.service.ExcelHelper;
import com.deltadental.pcp.calculation.service.PCPCalculationService;
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
@Validated
@NoArgsConstructor
public class ExcelPCPDataUploadController {

	@Autowired
	PCPCalculationService pcpCalculationService;

	@ApiOperation(value = PCPCalculationServiceConstants.SUMMARY_UPLOAD_MEMBERS_CONTRACTS_CLAIMS, notes = PCPCalculationServiceConstants.SUMMARY_UPLOAD_MEMBERS_CONTRACTS_CLAIMS_NOTES, response = String.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "Successfully uploaded pcp member claims.", response = String.class),
			@ApiResponse(code = 400, message = "Bad request.", response = ServiceError.class),
			@ApiResponse(code = 404, message = "Unable to upload pcp member claims.", response = ServiceError.class),
			@ApiResponse(code = 500, message = "Internal server error.", response = ServiceError.class) })
	@ResponseBody
	@MethodExecutionTime
	@PostMapping(value = PCPCalculationServiceConstants.UPLOAD_MEMBERS_CONTRACTS_CLAIMS_URI, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<MessageResponse> uploadPCPMemberClaims(@RequestParam("pcpMemberClaimsDataFile") MultipartFile pcpMemberClaimsDataFile) {
		log.info("START PCPCalculationServiceController.uploadPCPMemberClaims");
		MessageResponse messageResponse = MessageResponse.builder().build();
		if (ExcelHelper.hasExcelFormat(pcpMemberClaimsDataFile)) {
			List<MemberContractClaimRequest> validateProviderRequests = ExcelHelper.extractPCPMemberClaimsData(pcpMemberClaimsDataFile);
			if (!validateProviderRequests.isEmpty()) {
				validateProviderRequests.forEach(validateProviderRequest -> {
					validateProviderRequest.setOperatorId("FILE_UPLOAD");
					pcpCalculationService.stageMemberContractClaimRecord(validateProviderRequest);
				});
				messageResponse.setMessage("Successfully uploaded member contract claims!");
				log.info("Successfully uploaded member contract claims!");
			} else {
				messageResponse.setMessage("No member contract claims to upload in uploaded file.");
				log.info("No member contract claims to upload in uploaded file.");
			}
		} else {
			messageResponse.setMessage("Invalid excel data!");
			log.info("Invalid excel data!");
		}
		ResponseEntity<MessageResponse> responseEntity = new ResponseEntity<>(messageResponse, HttpStatus.OK);
		log.info("END PCPCalculationServiceController.uploadPCPMemberClaims");
		return responseEntity;
	}
}
