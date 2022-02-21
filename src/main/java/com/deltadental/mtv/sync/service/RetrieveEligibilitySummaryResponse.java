package com.deltadental.mtv.sync.service;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

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
public class RetrieveEligibilitySummaryResponse {

//	@JsonProperty(value = "return")
//	private RetrieveEligibilitySummaryReturnResponse _return;
	
    protected String contractID;
    protected String errorCode;
    protected String errorMsg;
    protected List<PcpEligbility> pcpEligbility;
}
