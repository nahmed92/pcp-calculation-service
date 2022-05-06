package com.deltadental.pcp.search.interservice.pojo;

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
public class BusinessLevels {
	
	private String businessLevel4;
	private String businessLevel5;
	private String businessLevel6;
	private String businessLevel7;
	private String effectiveDate;
	private String endDate;
	private String networkId;
	private String classCode;
	
}
