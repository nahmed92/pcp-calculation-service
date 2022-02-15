package com.deltadental.mtv.sync.service;

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
public class MemberDOB {
	private int day;
    private int eon;
    private int eonAndYear;
    private int fractionalSecond;
    private int hour;
    private int millisecond;
    private int minute;
    private int month;
    private int second;
    private int timezone;
    private boolean valid;
    private XmlschemaType xmlschemaType;
    private int year;
}
