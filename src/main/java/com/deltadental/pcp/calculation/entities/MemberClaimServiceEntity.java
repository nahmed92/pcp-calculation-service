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
@Table(name = "MEMBER_CLAIM_SERVICE", schema = "dbo")
public class MemberClaimServiceEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "contract_id")
	private String contractId;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "member_id")
	private String memberId;
	
	@Column(name = "provider_id")
	private String providerId;
	
	@Column(name = "operator_id")
	private String operatorId;
	
	@Column(name = "claim_id")
	private String claimId;

	@Column(name = "claim_status")
	private String claimStatus;
	
	@Column(name = "explanation_code")
	private String explanationCode;
	
	@Column(name = "procedure_code")
	private String procedureCode;
	
	@Column(name = "receive_date")
	private String receiveDate;
	
	@Column(name = "resolved_date")
	private String resolvedDate;
	
	@Column(name = "procedure_status")
	private String procedureStatus;
	

	@Column(name = "creation_ts")
	@CreationTimestamp
	private Date crationTs;

	@Column(name = "last_maint_ts")
	@UpdateTimestamp
	private Date lastMaintTs;
}
