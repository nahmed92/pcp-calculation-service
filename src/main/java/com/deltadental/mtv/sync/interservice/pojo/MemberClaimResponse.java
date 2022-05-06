package com.deltadental.mtv.sync.interservice.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberClaimResponse {

    private String billingProvId;
    private String businessLevel4;
    private String businessLevel5;
    private String businessLevel6;
    private String businessLevel7;
    private String claimId;
    private String claimSource;
    private String claimStatus;
    private String claimType;
    private String contractId;
    private String errorCode;
    private String errorMessage;
    private String divisionNumber;
    private String groupNumber;
  //  private XMLGregorianCalendar memberDOB;
    private String memberFirstName;
    private String memberID;
    private String memberLastName;
    private PaidTs paidTs;
    private String personId;
    private String providerId;
    private ReceivedTs receivedTs;
    private ResolvedTs resolvedTs;
    private String returnCode;
    private List<ServiceLine> serviceLines;
    private String servicesNumber;
}
