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
@ToString
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_claim", schema = "dbo")
public class MemberClaimEntity implements Serializable {

    private static final long serialVersionUID = -2354571047299351744L;

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "billing_provider_id")
    private String billingProviderId;

    @Column(name = "business_level_4")
    private String businessLevel4;

    @Column(name = "business_level_5")
    private String businessLevel5;

    @Column(name = "business_level_6")
    private String businessLevel6;

    @Column(name = "business_level_7")
    private String businessLevel7;

    @Column(name = "claim_source")
    private String claimSource;

    @Column(name = "claim_status")
    private String claimStatus;

    @Column(name = "claim_type")
    private String claimType;

    @Column(name = "group_number")
    private String groupNumber;

    @Column(name = "member_first_name")
    private String memberFirstName;

    @Column(name = "member_last_name")
    private String memberLastName;

    @Column(name = "paid_at")
    private Timestamp paidAt;

    @Column(name = "person_id")
    private String personId;

    @Column(name = "received_at")
    private Timestamp receivedAt;

    @Column(name = "resolved_at")
    private Timestamp resolvedAt;

    @Column(name = "services_number")
    private String servicesNumber;
	
	@Column(name = "creation_at", updatable = false)
	@CreationTimestamp
	private Timestamp crationAt;

	@Column(name = "last_updated_at")
	@UpdateTimestamp
	private Timestamp lastUpdatedAt;
	
	@Column(name = "operator_id")
	private String operatorId;

	@Column(name = "contract_member_claim_id")
	private String contractMemberClaimId;
	
	@Column(name = "contract_member_claim_sequence_id")
	private Integer contract_member_claim_sequence_id;

}
