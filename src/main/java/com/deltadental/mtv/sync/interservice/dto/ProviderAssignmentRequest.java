package com.deltadental.mtv.sync.interservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderAssignmentRequest {

	private String contractId;
	private String enrolleeNumber;
	private String pcpEffectiveDate;
	private String personId;
    private String practiceLocation;
    private String providerContFlag;
    private String providerId;
    private String reasonCode;
    private String sourceSystem;
    private String userId;
}
