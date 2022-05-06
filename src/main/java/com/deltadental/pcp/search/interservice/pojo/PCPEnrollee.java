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
public class PCPEnrollee {
	
	private String autoAssignmentState;
	private boolean batchAutoAssignFlag;
	private String benefitPackageID;
	private String businessLevel7;
	private boolean ccaOverrideFlag;
	private String dateOfBirth;
	private String divisionNumber;
	private BusinessLevels enrolleeBusinessLevels;
	private String groupInclusion;
	private String groupNumber;
	private String massTransfer;
	private Address memberAddress;
	private String memberLanguage ;	
	private String memberType;
	private String mtvPersonID;
	private String networkId;
	private String newMember;	
	private String overRideFlag;
	private String pcpEffectiveDate;
	private String pcpEndDate;
	private String pcpIdentifier;
	private String practiceLocation;
	private String randomSelection;
	private String sourceSystem;
	private String recordIdentifier;	
}
