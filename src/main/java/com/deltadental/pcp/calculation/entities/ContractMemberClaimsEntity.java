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
@Table(name = "CONTRACT_MEMBER_CLAIMS", schema = "dbo")
public class ContractMemberClaimsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CONTRACT_MEMBER_CLAIMS_ID", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer contractMemberClaimId;

	@Column(name = "CLAIM_ID")
	private String claimId;
	
	@Column(name = "CONTRACT_ID")
	private String contractId;
	
	@Column(name = "MEMBER_ID")
	private String memberId;
	
	@Column(name = "PROVIDER_ID")
	private String providerId;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "status")
//	@Type(type="org.hibernate.type.StringNVarcharType")
	private String status;
	
	@Column(name = "operator_id")
	private String operatorId;
	
	@Column(name = "creation_ts")
	@CreationTimestamp
	private Date crationTs;

	@Column(name = "last_maint_ts")
	@UpdateTimestamp
	private Date lastMaintTs;
}
