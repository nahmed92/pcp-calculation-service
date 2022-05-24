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
public class ServiceLine {
	private String claimType;
    private String encounterFlag;
    private String explnCode;
    private String procedureCode;
    private String sequenceNumber;
    private String serviceNumber;
    private ServicePaidTs servicePaidTs;
    private ServiceResolutionTs serviceResolutionTs;
    private String fromDate;
    private String thruDate;
}
