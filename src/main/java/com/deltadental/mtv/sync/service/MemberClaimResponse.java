package com.deltadental.mtv.sync.service;

import java.time.LocalDate;
import java.util.List;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
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
    private String groupNumber;
  //  private XMLGregorianCalendar memberDOB;
    private String memberFirstName;
    private String memberID;
    private String memberLastName;
    private Timestamp paidTs;
    private String personId;
    private String providerId;
    private Timestamp receivedTs;
    private Timestamp resolvedTs;
    private String returnCode;
    private List<ServiceLine> serviceLines;
    private String servicesNumber;
}
