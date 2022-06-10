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
public class Enrollee {
    private String mbrClassCode;
    private String mbrFirstName;
    private String mbrLastName;
    private String mbrMiddleName;
    private String memberId;
    private String originalEffectiveDate;
    private String personBirthDate;
    private String personId;
}
