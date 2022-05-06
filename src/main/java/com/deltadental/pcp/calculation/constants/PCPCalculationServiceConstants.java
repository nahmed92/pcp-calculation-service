package com.deltadental.pcp.calculation.constants;

public final class PCPCalculationServiceConstants {

	private PCPCalculationServiceConstants() {
		
	}
	
	public static final String SUMMARY_VALIDATE_PROVIDER = "Validate Provider";
	public static final String SUMMARY_VALIDATE_PROVIDER_NOTES = "This API validates the provider.";
	public static final String VALIDATE_PROVIDER_URI = "/validate-provider";
	
	public static final String PROCESS_PCP_MEMBER_CONTRACT = "Process PCP Member Contract";
	public static final String PROCESS_PCP_MEMBER_CONTRACT_NOTES = "This API Process Member Contract.";
	public static final String PROCESS_PCP_MEMBER_CONTRACT_URI = "/process-pcp-member-contract";
	
	public static final String SUMMARY_MEMBER_CONTRACT_CLAIM = "Member Contract Claim information for pcp assignment";
	public static final String SUMMARY_MEMBER_CONTRACT_CLAIM_NOTES = "This API Stages Member Contract Claim information for pcp assignment.";
	public static final String MEMBER_CONTRACT_CLAIM_URI = "/member-contract-claim";
	
	public static final String SUMMARY_UPLOAD_MEMBERS_CONTRACTS_CLAIMS = "Upload Multiple Members and Contracts and Claims in excel sheet for pcp assignment.";
	public static final String SUMMARY_UPLOAD_MEMBERS_CONTRACTS_CLAIMS_NOTES = "This API helps to upload Multiple Members and Contracts and Claims in excel sheet for pcp assignment.";
	public static final String UPLOAD_MEMBERS_CONTRACTS_CLAIMS_URI = "/excel/upload/members-contracts-and-claims";
	
	public static final String SUMMARY_MEMBERS_CONTRACTS_CLAIMS = "Multiple Members and Contracts and Claims in single request for pcp assignment.";
	public static final String SUMMARY_MEMBERS_CONTRACTS_CLAIMS_NOTES = "This API will stage recirds for Multiple Members and Contracts and Claims in single request for pcp assignment.";
	public static final String ASSIGN_MEMBERS_CONTRACTS_CLAIMS_URI = "/members-contracts-and-claims";
}
