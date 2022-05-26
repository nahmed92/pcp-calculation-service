package com.deltadental.mtv.sync.interservice.dto;

import java.sql.Timestamp;

import com.deltadental.pcp.calculation.util.TimestampDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceLine {
	private String claimType;
    private String encounterFlag;
    private String explnCode;
    private String procedureCode;
    private String sequenceNumber;
    private String serviceNumber;
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp servicePaidTs;
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp serviceResolutionTs;
    private String fromDate;
    private String thruDate;
}
