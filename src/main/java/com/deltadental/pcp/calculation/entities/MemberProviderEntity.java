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
@org.hibernate.annotations.Entity(
        dynamicUpdate = true
)
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
	
	@Column(name = "REASON_CD")
	private String reasonCd;

	@Column(name = "SOURCE_SYSTEM")
	private String sourceSystem;

	@Column(name = "STATUS")
	private String status;
	
//	@CreationTimestamp
	@Column(name = "CREATION_TS", nullable = false, updatable = false)
	private Timestamp crationTs;

//	@UpdateTimestamp
	@Column(name = "LAST_MAINT_TS", nullable = false)
	private Timestamp lastMaintTs;
	
	@Column(name = "OPERATOR_ID")
	private String operatorId;
	
	@Column(name = "CONTRACT_MEMBER_CLAIMS_ID")
	private Integer contractMemberClaimsId;	
	
	@Column(name = "PERSON_ID")
    private String personId;
	
	@Column(name = "MEMBER_ID")
	private String memberId;

	@Column(name = "CONTRACT_ID")
	private String contractId;
	
	@Column(name = "PROVIDER_ID")
	private String providerId;
	
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
