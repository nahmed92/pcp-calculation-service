package com.deltadental.pcp.calculation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberContractClaimResponse {

	private String claimId;
	private String contractId;
	private String memberId;
	private String providerId;
	private String status;
	private String pcpEffectiveDate;
}
