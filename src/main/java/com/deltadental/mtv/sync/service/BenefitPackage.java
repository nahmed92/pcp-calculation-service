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
public class BenefitPackage {
	private String benefitPackageId;
    private String bpDesc;
    private String bpDivisionName;
    private String bpDivisionNumber;
    private String bpEffectiveDate;
    private String bpEndDate;
    private String bpGroupName;
    private String bpGroupNumber;
    private String bpName;
}
