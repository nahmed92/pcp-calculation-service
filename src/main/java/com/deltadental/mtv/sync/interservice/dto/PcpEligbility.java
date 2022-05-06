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
public class PcpEligbility {

	private String benefitPackageId;
    private String contractEndDate;
    private String divisionName;
    private String divisionNumber;
    private String effectiveDate;
    private String enrolleeNumber;
    private String financialReportingStateIdentifier;
    private String groupName;
    private String groupNumber;
    private String groupTypeIdentifier;
    private String healthCareContractHolderIdentifier;
    private String product;
    private String status;

}
