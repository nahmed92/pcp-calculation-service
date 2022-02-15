package com.deltadental.mtv.sync.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

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
public class ServiceLine {
	private String claimType;
    private String encounterFlag;
    private String explnCode;
    private String procedureCode;
    private String sequenceNumber;
    private String serviceNumber;
    private ServicePaidTs servicePaidTs;
    private ServiceResolutionTs serviceResolutionTs;
}
