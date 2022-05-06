package com.deltadental.pcp.calculation.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CONTRACT_MEMBER_CLAIMS", schema = "dbo")
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class ContractMemberClaimsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	// FIXME:
	@Id
	@Column(name = "CONTRACT_MEMBER_CLAIMS_ID", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer contractMemberClaimId;

	@Column(name = "CLAIM_ID", nullable = false, updatable = false)
	private String claimId;

	@Column(name = "CONTRACT_ID", nullable = false, updatable = false)
	private String contractId;

	@Column(name = "MEMBER_ID", nullable = false, updatable = false)
	private String memberId;

	@Column(name = "PROVIDER_ID", nullable = false, updatable = false)
	private String providerId;

	@Column(name = "STATE", nullable = false, updatable = false)
	private String state;

	@Column(name = "STATUS", nullable = true, updatable = true)
	private String status;

	@Column(name = "operator_id", updatable = false)
	private String operatorId;

//	@CreationTimestamp
	@Column(name = "CREATION_TS", nullable = false, updatable = false)
	private Timestamp crationTs;

//	@UpdateTimestamp
	@Column(name = "LAST_MAINT_TS", nullable = false)
	private Timestamp lastMaintTs;

	@Column(name = "INSTANCE_ID", updatable = false)
	private String instanceId;

	@Column(name = "ERROR_MESSAGE", updatable = true)
	private String errorMessage;

	@PrePersist
	public void onInsert() {
		crationTs = Timestamp.from(ZonedDateTime.now(ZoneId.of("America/Los_Angeles")).toInstant());
		lastMaintTs = crationTs;
	}

	@PreUpdate
	public void onUpdate() {
		lastMaintTs = Timestamp.from(ZonedDateTime.now(ZoneId.of("America/Los_Angeles")).toInstant());
	}
}
