package com.deltadental.mtv.sync.service;

import java.util.List;

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
public class UpdatePCPRequest {

	private String actionIndicator;
    private List<BenefitPackage> benefitPackage;
    private String benefitPackageId;
    private String contractID;
    private String divsionNumber;
    private String enrolleeNumber;
    private String errorCode;
    private String errorMessage;
    private String groupNumber;
    private List<GroupProdNetwork> groupProdNetwork;
    private String pcpEffectiveDate;
    private String pcpEndDate;
    private String personID;
    private String practiceLocation;
    private String provBusContId;
    private String provQualId;
    private String providerContFlag;
    private String providerContractId;
    private String providerID;
    private String reasonCode;
    private String receiptDate;
    private String returnCode;
    private String sourceSystem;
    private String userId;
}
