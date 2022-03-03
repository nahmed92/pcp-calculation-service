package com.deltadental.pcp.calculation.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
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
@Immutable
@Table(name = "member_claim_services", schema = "dbo")
public class MemberClaimServicesEntity implements Serializable {

	private static final long serialVersionUID = 2757500429236458720L;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "claim_id")
	private String claimId;
	
	@Column(name = "claim_type")
	private String claimType;
	
	@Column(name = "encounterFlag")
	private String encounter_flag;
	
	@Column(name = "contract_id")
	private String contractId;

	@Column(name = "member_id")
	private String memberId;

	@Column(name = "provider_id")
	private String providerId;

	@Column(name = "explanation_code")
	private String explanationCode;
	
	@Column(name = "procedure_code")
	private String procedureCode;
	
	@Column(name = "sequence_number")
	private String sequenceNumber;
	
	@Column(name = "service_number")
	private String serviceNumber;
	
	@Column(name = "service_paid_ts")
	@CreationTimestamp
	private Date servicePaidTs;

	@Column(name = "service_resolution_ts")
	@UpdateTimestamp
	private Date serviceResolutionTs;

	@Column(name = "creation_ts")
	@CreationTimestamp
	private Date crationTs;

	@Column(name = "last_maint_ts")
	@UpdateTimestamp
	private Date lastMaintTs;

	@Column(name = "operator_id")
	private String operatorId;
	
}
