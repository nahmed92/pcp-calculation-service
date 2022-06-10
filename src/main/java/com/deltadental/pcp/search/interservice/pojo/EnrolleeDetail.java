package com.deltadental.pcp.search.interservice.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrolleeDetail {

    private String memberType;
    private String groupNumber;
    private String divisionNumber;
    private List<BusinessLevels> businessLevels;
    private List<BenefitPackage> benefitPackages;
    private List<PcpProvider> pcpProviders;
    private String enrolleeStatusCode;
    private boolean pcpValidationFlag; //This is for EEP and IP to determine if the input provider is valid
    private List<String> errorMessages;
    private String recordIdentifier;
    private String numberOfFacilities;
}
