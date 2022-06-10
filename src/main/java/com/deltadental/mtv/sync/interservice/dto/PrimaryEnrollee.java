package com.deltadental.mtv.sync.interservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrimaryEnrollee {

    private List<Eligibility> eligibility;
    private String ethinicityCode;
    private String individualRelationshipCode;
    private String languageCode;
    private String memberId;
    private List<Mpna> mpna;
    private String originalEffectiveDate;
    private Person person;
    private String relationshipCode;
    private List<SecondaryIdentifiersList> secondaryIdentifiersList;
}
