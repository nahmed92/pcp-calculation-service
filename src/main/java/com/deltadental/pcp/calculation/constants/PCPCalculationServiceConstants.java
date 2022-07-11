package com.deltadental.pcp.calculation.constants;

import java.util.List;

import com.deltadental.pcp.calculation.enums.Status;

public final class PCPCalculationServiceConstants {

    private PCPCalculationServiceConstants() {

    }
    public static final String SUMMARY_UPLOAD_MEMBERS_CONTRACTS_CLAIMS = "Upload Multiple Members and Contracts and Claims in excel sheet for pcp assignment.";
    public static final String SUMMARY_UPLOAD_MEMBERS_CONTRACTS_CLAIMS_NOTES = "This API helps to upload Multiple Members and Contracts and Claims in excel sheet for pcp assignment.";
    public static final String UPLOAD_MEMBERS_CONTRACTS_CLAIMS_URI = "/excel/upload/claim-data";

    public static final String SUMMARY_MEMBERS_CONTRACTS_CLAIMS = "Multiple Members and Contracts and Claims in single request for pcp assignment.";
    public static final String SUMMARY_MEMBERS_CONTRACTS_CLAIMS_NOTES = "This API will stage records for Multiple Members and Contracts and Claims in single request for pcp assignment.";
    
    
    public static final List<Status> SEARCH_STATUS_VALIDATE_PENDING = List.of(Status.RETRY, Status.STAGED, Status.VALIDATED, Status.PCP_EXCLUDED, Status.PCP_NOT_INCLUDED);
    public static final List<Status> SEARCH_STATUS_SAVE =             List.of(Status.RETRY, Status.STAGED, Status.VALIDATED, Status.PCP_EXCLUDED, Status.PCP_NOT_INCLUDED, Status.PCP_ASSIGNED, Status.PCP_ALREADY_ASSIGNED, Status.PCP_SKIPPED);
}
