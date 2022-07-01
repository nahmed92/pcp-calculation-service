package com.deltadental.pcp.calculation.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_provider", schema = "dbo")
public class MemberProviderEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "claim_status")
    private String claimStatus;

    @Column(name = "pcp_effective_date")
    private String pcpEffectiveDate;

    @Column(name = "reason_code")
    private String reasonCode;

    @Column(name = "source_system")
    private String sourceSystem;

    @Column(name = "status")
    private String status;

    @Column(name = "operator_id")
    private String operatorId;

    @Column(name = "person_id")
    private String personId;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "contract_id")
    private String contractId;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "practice_location_id")
    private String practiceLocationId;

    @Column(name = "provider_contract_id")
    private String providerContractId;

    @Column(name = "business_level_assn_id")
    private String businessLevelAssnId;

    @Column(name = "provider_qualifier_id")
    private String providerQualifierId;

    @CreationTimestamp
    @Column(name = "creation_at", updatable = false)
    private Timestamp creationAt;

    @UpdateTimestamp
    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;
    
	@Column(name = "contract_member_claim_id")
	private String contractMemberClaimId;
	
	@Column(name = "contract_member_claim_sequence_id")
	private Integer contractMemberClaimSequenceId;

}
