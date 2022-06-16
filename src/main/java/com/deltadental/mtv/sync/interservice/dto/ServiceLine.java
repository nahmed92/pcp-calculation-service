package com.deltadental.mtv.sync.interservice.dto;

import com.deltadental.pcp.calculation.util.TimestampDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

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
    @JsonFormat(shape = Shape.STRING, pattern = "MM-dd-yyyy", timezone = JsonFormat.DEFAULT_TIMEZONE)
    private Date fromDate;
    @JsonFormat(shape = Shape.STRING, pattern = "MM-dd-yyyy", timezone = JsonFormat.DEFAULT_TIMEZONE)
    private Date thruDate;
}
