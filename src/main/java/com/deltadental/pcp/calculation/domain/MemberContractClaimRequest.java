package com.deltadental.pcp.calculation.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Valid
public class MemberContractClaimRequest {

	@NotEmpty(message = "Claim Id cannot be null or empty!")
	private String claimId;
	
	@NotEmpty(message = "Contract Id cannot be null or empty!")
	private String contractId;
	
	@NotEmpty(message = "Member Id cannot be null or empty!")
	@Size(max = 2, min = 2, message = "Member ID length must be two(2).")
	private String memberId;
	
	@NotEmpty(message = "Provider Id cannot be null or empty!")
	private String providerId;
	
	@NotEmpty(message = "State cannot be null or empty!")
	@Size(max = 2, min = 2, message = "State code length must be two(2).")
	private String state;
	
	private String operatorId;
}
