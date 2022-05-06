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
public class GroupProdNetwork {
	private String divisionNumber;
    private String financialReportingStateIdentifier;
    private String gpnaEffectiveDate;
    private String gpnaEndDate;
    private String groupNumber;
    private String groupTypeIdentifer;
    private String healthCareContractHolderIdentifier;
    private String networkIdentifier;
    private String productIdentifier;
}
