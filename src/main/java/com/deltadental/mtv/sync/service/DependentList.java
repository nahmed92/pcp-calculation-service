package com.deltadental.mtv.sync.service;

import java.util.List;

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
public class DependentList {
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
