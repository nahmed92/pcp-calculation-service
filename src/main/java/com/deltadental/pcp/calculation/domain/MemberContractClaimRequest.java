package com.deltadental.pcp.calculation.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Valid
public class MemberContractClaimRequest {

	@NotBlank(message = "Claim Id cannot be null or empty!")
	private String claimId;

	@NotBlank(message = "Contract Id cannot be null or empty!")
	private String contractId;

	@NotBlank(message = "Member Id cannot be null or empty!")
	@Size(max = 2, min = 2, message = "Member ID length must be two(2).")
	private String memberId;

	@NotBlank(message = "Provider Id cannot be null or empty!")
	private String providerId;

	@NotBlank(message = "State cannot be null or empty!")
	@Size(max = 2, min = 2, message = "State code length must be two(2).")
	private String state; //FIXME: Change to enum

	private String operatorId;
}
