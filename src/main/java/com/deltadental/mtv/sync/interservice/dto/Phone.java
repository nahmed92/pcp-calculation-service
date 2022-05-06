package com.deltadental.mtv.sync.interservice.dto;

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
public class Phone {
	private String actionFlag;
    private String countryCode;
    private String extension;
    private String pagerPinId;
    private String phoneNumber;
    private String phoneType;
    private String phoneTypeDescription;
    private boolean primaryPhone;
}
