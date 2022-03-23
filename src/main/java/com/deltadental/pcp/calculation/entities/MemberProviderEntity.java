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
import org.hibernate.annotations.Type;
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
@Table(name = "MEMBER_PROVIDER", schema = "dbo")
public class MemberProviderEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "MEMBER_PROVIDER_ID", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memberProviderId;
	
	@Column(name = "CLAIM_STATUS")
	private String claimStatus;

	@Column(name = "PCP_EFFECTIVE_DATE")
	private String pcpEffectiveDate;
	
	@Column(name = "PCP_END_DATE")
	private String pcpEndDate;

	@Column(name = "REASON_CD")
	private String reasonCd;

	@Column(name = "SOURCE_SYSTEM")
	private String sourceSystem;

	@Column(name = "STATUS")
	@Type(type="org.hibernate.type.StringNVarcharType")
	private String status;

	@Column(name = "ZIP_CODE")
	private String zipCode;
	
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
