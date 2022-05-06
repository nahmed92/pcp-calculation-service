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
public class ProviderAssignmentRequest {

	private String contractID;
    private String errorCode;
    private String errorMessage;
    private String personID;
    private String returnCode;
	private String enrolleeNumber;
    private String pcpEffectiveDate;
    private String pcpEndDate;
    private String practiceLocation;
    private String providerContFlag;
    private String providerID;
    private String reasonCode;
    private String sourceSystem;
    private String userId;
}
