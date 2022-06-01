package com.deltadental.pcp.calculation.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.deltadental.pcp.calculation.constants.PCPCalculationServiceConstants;
import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.service.ExcelService;
import com.deltadental.pcp.calculation.service.MemberContractClaimService;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.deltadental.platform.common.exception.ServiceError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/pcp-calculation")
@Api(value = "/pcp-calculation")
@Slf4j
public class ExcelPCPDataUploadController {

	@Autowired
	private MemberContractClaimService memberContractClaimService;

	@Autowired
	private ExcelService excelService;

	@ApiOperation(value = PCPCalculationServiceConstants.SUMMARY_UPLOAD_MEMBERS_CONTRACTS_CLAIMS, notes = PCPCalculationServiceConstants.SUMMARY_UPLOAD_MEMBERS_CONTRACTS_CLAIMS_NOTES, response = String.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "Successfully uploaded pcp member claims.", response = String.class),
			@ApiResponse(code = 400, message = "Bad request.", response = ServiceError.class),
			@ApiResponse(code = 404, message = "Unable to upload pcp member claims.", response = ServiceError.class),
			@ApiResponse(code = 500, message = "Internal server error.", response = ServiceError.class) })
	@ResponseBody
	@MethodExecutionTime
	@PostMapping(value = PCPCalculationServiceConstants.UPLOAD_MEMBERS_CONTRACTS_CLAIMS_URI, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> uploadPCPMemberClaims(
			@RequestParam(name = "pcpMemberClaimsDataFile", required = true) MultipartFile pcpMemberClaimsDataFile) {
		log.info("START PCPCalculationServiceController.uploadPCPMemberClaims");
		ResponseEntity<String> responseEntity = null;
		if (excelService.hasExcelFormat(pcpMemberClaimsDataFile)) {
			List<MemberContractClaimRequest> memberContractClaimRequests = excelService
					.extractPCPMemberClaimsData(pcpMemberClaimsDataFile);
			if (CollectionUtils.isNotEmpty(memberContractClaimRequests)) {
				memberContractClaimService.stageMemberContractClaimRecords(memberContractClaimRequests);
				responseEntity = new ResponseEntity<>("Successfully uploaded member contract claims!",
						HttpStatus.CREATED);
				log.info("Successfully uploaded member contract claims!");
			} else {
				log.info("No member contract claims to upload in uploaded file.");
				responseEntity = new ResponseEntity<>("No member contract claims to upload in uploaded file.",
						HttpStatus.BAD_REQUEST);
			}
		} else {
			log.info("Invalid excel data!");
			responseEntity = new ResponseEntity<>("Invalid excel data!", HttpStatus.BAD_REQUEST);
		}
		log.info("END PCPCalculationServiceController.uploadPCPMemberClaims");
		return responseEntity;
	}
}