package com.deltadental.mtv.sync.interservice.pojo;

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
public class CoverageSpan {
	private String disenrollmentReason;
    private String divisionName;
    private String divisionNumber;
    private String effectiveDate;
    private String endDate;
    private String groupName;
    private String groupNumber;
    private String ratePackageIdentifierCode;
    private String receiptDate;
}
