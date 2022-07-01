package com.deltadental.mtv.sync.interservice.dto;

import com.deltadental.pcp.calculation.util.TimestampDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private String memberFirstName;
    private String memberID;
    private String memberLastName;
    private Date fromDate;
    private Date thruDate;
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp paidTs;
    private String personId;
    private String providerId;
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp receivedTs;
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp resolvedTs;
    private String returnCode;
    private List<ServiceLine> serviceLines;
    private String servicesNumber;
}
