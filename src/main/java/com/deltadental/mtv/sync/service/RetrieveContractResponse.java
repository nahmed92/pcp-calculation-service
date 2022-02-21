package com.deltadental.mtv.sync.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RetrieveContractResponse {

	private String actionIndicator;
    private String contractId;
    private List<CoverageSpan> coverageSpans;
    private List<Enrollee> dependentEnrollee;
    private List<DependentList> dependentList;
    private String dmx;
    private Employerinfo employerinfo;
    private Enrollee enrollee;
    private String errorCode;
    private String errorMessage;
    private PrimaryEnrollee primaryEnrollee;
    private String primaryEnrolleeContractIdentifier;
    private String receiptDate;
    private String returnCode;
    private List<String> warningMessages;
}
