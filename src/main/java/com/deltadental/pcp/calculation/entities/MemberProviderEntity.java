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

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Immutable
@Table(name = "MEMBER_PROVIDER", schema = "dbo")
public class MemberProviderEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "pcp_identifier")
	private String pcpIdentifier;

	@Column(name = "zip_code")
	private String zipCode;

	@Column(name = "member_id")
	private String memberId;
	
	@Column(name = "member_type")
	private String memberType;
	
	@Column(name = "contract_id")
	private String contractId;
	
	@Column(name = "source_system")
	private String sourceSystem;

	@Column(name = "status")
	private String status;

	@Column(name = "reason_cd")
	private String reasonCd;
	
	@Column(name = "pcp_effective_date")
	private String pcpEffectiveDate;

	@Column(name = "claim_id")
	private String claimId;
	
	@Column(name = "claim_status")
	private String claimStatus;

	@Column(name = "creation_ts")
	@CreationTimestamp
	private Date crationTs;

	@Column(name = "last_maint_ts")
	@UpdateTimestamp
	private Date lastMaintTs;
}
