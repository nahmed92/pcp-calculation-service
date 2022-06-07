package com.deltadental.pcp.calculation.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_claim_service", schema = "dbo")  //FIXME: remove service sufix
public class MemberClaimServicesEntity implements Serializable {

	private static final long serialVersionUID = 2757500429236458720L;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	private String id;

	@Column(name = "claim_type")
	private String claimType;

	@Column(name = "encounter_flag")
	private String encounterFlag;

	@Column(name = "expln_code")
	private String explnCode;

	@Column(name = "procedure_code")
	private String procedureCode;

	@Column(name = "sequence_number")
	private String sequenceNumber;

	@Column(name = "service_number")
	private String serviceNumber;

	@Column(name = "service_paid_at")
	private Date servicePaidAt;

	@Column(name = "service_resolution_at")
	private Date serviceResolutionAt;
	
	@Column(name = "fromDate")
	private Date fromDate;
	
	@Column(name = "thruDate")
	private Date thruDate;

	@CreationTimestamp
	@Column(name = "creation_at", updatable = false)
	private Timestamp crationAt;

	@UpdateTimestamp
	@Column(name = "last_update_at")
	private Timestamp lastUpdatedAt;

	@Column(name = "operator_id")
	private String operatorId;

	@Column(name = "member_claim_id")
	private String memberClaimId;

}
