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
@Table(name = "MEMBER_CLAIM", schema = "dbo")
public class MemberClaimEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "MEMBER_CLAIM_ID", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memberClaimId;
	
	@Column(name = "BILLING_PROV_ID")
	private String billingProvId;
	
	@Column(name = "BUSINESS_LEVEL_4")
    private String businessLevel4;
	
	@Column(name = "BUSINESS_LEVEL_5")
    private String businessLevel5;
	
	@Column(name = "BUSINESS_LEVEL_6")
    private String businessLevel6;
	
	@Column(name = "BUSINESS_LEVEL_7")
    private String businessLevel7;
	
	@Column(name = "CLAIM_SOURCE")
    private String claimSource;
	
	@Column(name = "CLAIM_STATUS")
    private String claimStatus;
	
	@Column(name = "CLAIM_TYPE")
    private String claimType;
	
	@Column(name = "GROUP_NUMBER")
    private String groupNumber;
	
	@Column(name = "MEMBER_FIRST_NAME")
    private String memberFirstName;

	@Column(name = "MEMBER_LAST_NAME")
	private String memberLastName;
	
	@Column(name = "PAID_TS")
    private Timestamp paidTs;
	
	@Column(name = "PERSON_ID")
    private String personId;
	
	@Column(name = "RECEIVED_TS")
    private Timestamp receivedTs;
	
	@Column(name = "RESOLVED_TS")
    private Timestamp resolvedTs;

	@Column(name = "SERVICES_NUMBER")
    private String servicesNumber;
	
	@Column(name = "CREATION_TS")
	@CreationTimestamp
	private Date crationTs;

	@Column(name = "LAST_MAINT_TS")
	@UpdateTimestamp
	private Date lastMaintTs;
	
	@Column(name = "OPERATOR_ID")
	private String operatorId;

	@Column(name = "CONTRACT_MEMBER_CLAIMS_ID")
	private Integer contractMemberClaimsId;
}
