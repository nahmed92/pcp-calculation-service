package com.deltadental.pcp.calculation.controller;

import com.deltadental.pcp.calculation.constants.PCPCalculationServiceConstants;
import com.deltadental.pcp.calculation.domain.MemberContractClaimRequest;
import com.deltadental.pcp.calculation.service.MemberContractClaimService;
import com.deltadental.platform.common.exception.ServiceError;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/pcp/calculation")
@Api(value = "/pcp/calculation")
@Slf4j
@Validated
public class MemberContractClaimController {

    @Autowired
    private MemberContractClaimService memberContractClaimService;

    @ApiOperation(value = PCPCalculationServiceConstants.SUMMARY_MEMBERS_CONTRACTS_CLAIMS, notes = PCPCalculationServiceConstants.SUMMARY_MEMBERS_CONTRACTS_CLAIMS_NOTES, response = Boolean.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Save Member Contract Claims and assign primary care providers to members.", response = Boolean.class),
            @ApiResponse(code = 400, message = "Bad request.", response = ServiceError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ServiceError.class)})
    @ResponseBody
    @PostMapping(value = "/member-contract-claims", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> stageMembersContractsAndClaims(
            @Valid @RequestBody List<MemberContractClaimRequest> memberContractClaimRequests) {
        log.info("START MemberContractClaimController.stageMembersContractsAndClaims");
        ResponseEntity<Boolean> responseEntity;
        if (CollectionUtils.isNotEmpty(memberContractClaimRequests)) {
            memberContractClaimService.stageMemberContractClaimRecords(memberContractClaimRequests);
            responseEntity = new ResponseEntity<>(Boolean.TRUE, HttpStatus.CREATED);
        } else {
            responseEntity = new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
        log.info("END MemberContractClaimController.stageMembersContractsAndClaims");
        return responseEntity;
    }

}
