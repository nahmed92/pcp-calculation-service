package com.deltadental.pcp.search.service;

import java.util.List;

import com.deltadental.pcp.search.service.pojos.PCPResponse;
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
public class PCPValidateResponse {
	
	private String sourceSystem;
	private String processStatusDescription;
	private String processStatusCode;
	private List<PCPResponse> pcpResponses;  
   
}
