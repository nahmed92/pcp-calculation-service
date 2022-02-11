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
public class Address {
	private String actionFlag;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressTypeCode;
    private String city;
    private String country;
    private String county;
    private String state;
    private String zip;
}
