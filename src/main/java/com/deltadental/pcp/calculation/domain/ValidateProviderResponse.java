package com.deltadental.pcp.calculation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ValidateProviderResponse {

	private String claimId;
	private String contractId;
	private String memberId;
	private String providerId;
	private String status;
	private String pcpEffectiveDate;
}
