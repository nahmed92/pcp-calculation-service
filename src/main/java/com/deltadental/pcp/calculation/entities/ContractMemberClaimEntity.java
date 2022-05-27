package com.deltadental.pcp.calculation.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.deltadental.pcp.calculation.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "contract_member_claim", schema = "dbo")
@Entity
public class ContractMemberClaimEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	private String id;

	@Column(name = "claim_id")
	private String claimId;

	@Column(name = "contract_id")
	private String contractId;

	@Column(name = "member_id")
	private String memberId;

	@Column(name = "provider_id")
	private String providerId;

	@Column(name = "state")
	private String state;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;
	
	
	@Column(name = "operator_id")
	private String operatorId;

	@Column(name = "instance_id")
	private String instanceId;

	@Column(name = "error_message")
	private String errorMessage;

	@CreationTimestamp
	@Column(name = "created_at")
	private Timestamp crationAt;

	@UpdateTimestamp
	@Column(name = "last_updated_at")
	private Timestamp lastUpdatedAt;
	
}
