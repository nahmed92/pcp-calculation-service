package com.deltadental.mtv.sync.interservice.pojo;

import java.util.List;

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
public class RetrieveEligibilitySummaryResponse {
	
    protected String contractID;
    protected String errorCode;
    protected String errorMsg;
    protected List<PcpEligbility> pcpEligbility;
}
