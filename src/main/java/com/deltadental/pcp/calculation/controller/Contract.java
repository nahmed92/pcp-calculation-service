package com.deltadental.pcp.calculation.controller;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class Contract {

	private String contractID;
	
	private List<String> memberId;
	
	private String groupNumber;
	
    private String divisionNumber;

	private String contract;

	private Integer numberOfEnrollee;

	private STATUS status;
	
	private Timestamp assignmentDate;
	
	private Integer numOfAttempt;

	private Timestamp createdDate;

	private Timestamp lastUpdatedDate;
}
