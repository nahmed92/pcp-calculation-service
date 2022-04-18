package com.deltadental.pcp.calculation.entities;

import java.io.Serializable;
import java.sql.Timestamp;

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
@Table(name = "CONTRACT_MEMBER_CLAIMS", schema = "dbo")
@Entity
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

	@Column(name = "STATE")
	private String state;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "operator_id")
	private String operatorId;

	@CreationTimestamp
	@Column(name = "CREATION_TS", nullable = false, updatable = false)
	private Timestamp crationTs;

	@UpdateTimestamp
	@Column(name = "LAST_MAINT_TS", nullable = false)
	private Timestamp lastMaintTs;

	@Column(name = "INSTANCE_ID")
	private String instanceId;
	
	@Column(name = "ERROR_MESSAGE")
	private String errorMessage;
}
