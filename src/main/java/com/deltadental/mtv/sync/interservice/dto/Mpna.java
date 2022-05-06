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
public class Mpna {
	private String effectiveDate;
    private String endDate;
    private String networkidentifier;
    private String practiceLocation;
    private String providerIdentifier;
    private String providerName;
    private String providerReasonCode;
}
