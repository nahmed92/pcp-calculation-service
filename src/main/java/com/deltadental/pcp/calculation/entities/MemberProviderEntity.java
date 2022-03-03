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
@Table(name = "MEMBER_PROVIDER", schema = "dbo")
public class MemberProviderEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "claim_id")
	private String claimId;
	
	@Column(name = "claim_status")
	private String claimStatus;
	
	@Column(name = "contract_id")
	private String contractId;
	
	@Column(name = "zip_code")
	private String zipCode;

	@Column(name = "member_id")
	private String memberId; 

	@Column(name = "pcp_effective_date")
	private String pcpEffectiveDate;
	
	@Column(name = "pcp_end_date")
	private String pcpEndDate;

	@Column(name = "pcp_identifier")
	private String pcpIdentifier;
	
	@Column(name = "reason_cd")
	private String reasonCd;

	@Column(name = "source_system")
	private String sourceSystem;

	@Column(name = "status")
	private String status;

	@Column(name = "creation_ts")
	@CreationTimestamp
	private Date crationTs;

	@Column(name = "last_maint_ts")
	@UpdateTimestamp
	private Date lastMaintTs;
	
	@Column(name = "operator_id")
	private String operatorId;
}
