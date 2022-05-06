package com.deltadental.pcp.search.interservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PCPValidateRequest {

	private String contractId;
    private String lookAheadDays;
    private String memberType;
    private String mtvPersonId;
    private String pcpEffDate;
    private String pcpEndDate;
    private String product;
    private String providerId;
    private String recordIdentifier;
    private String sourceSystem;
}
