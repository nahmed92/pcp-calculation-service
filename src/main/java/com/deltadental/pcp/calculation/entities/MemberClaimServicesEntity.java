package com.deltadental.pcp.calculation.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Entity
@Table(name = "MEMBER_CLAIM_SERVICES", schema = "dbo")
public class MemberClaimServicesEntity implements Serializable {

	private static final long serialVersionUID = 2757500429236458720L;

	@Id
	@Column(name = "MEMBER_CLAIM_SERVICES_ID", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memberClaimServicesId;

	@Column(name = "CLAIM_TYPE")
	private String claimType;
	
	@Column(name = "ENCOUNTER_FLAG")
	private String encounterFlag;

	@Column(name = "EXPLN_CODE")
	private String explnCode;
	
	@Column(name = "PROCEDURE_CODE")
	private String procedureCode;
	
	@Column(name = "SEQUENCE_NUMBER")
	private String sequenceNumber;
	
	@Column(name = "SERVICE_NUMBER")
	private String serviceNumber;
	
	@Column(name = "SERVICE_PAID_TS")
	@CreationTimestamp
	private Date servicePaidTs;

	@Column(name = "SERVICE_RESOLUTION_TS")
	@UpdateTimestamp
	private Date serviceResolutionTs;

	@CreationTimestamp
	@Column(name = "CREATION_TS", nullable = false, updatable = false)
	private Timestamp crationTs;

	@UpdateTimestamp
	@Column(name = "LAST_MAINT_TS", nullable = false)
	private Timestamp lastMaintTs;

	@Column(name = "OPERATOR_ID")
	private String operatorId;
	
	@Column(name = "MEMBER_CLAIM_ID")
	private Integer memberClaimId;	
}
