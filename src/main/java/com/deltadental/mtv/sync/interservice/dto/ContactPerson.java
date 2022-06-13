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
public class ContactPerson {
    private String actionFlag;
    private Address contactAddress;
    private String contactEffectiveDate;
    private String contactEndDate;
    private String contactFirstName;
    private String contactLastName;
    private String contactMiddleName;
    private String contactSuffix;
    private String contactTypeCode;
    private String email;
    private Phone phone;
    private String whoIdentifier;
    private String whoTypeCode;
}
