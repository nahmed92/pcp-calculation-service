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
	@Column(name = "id", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "billing_prov_id")
	private String billingProvId;
	
	@Column(name = "business_level_4")
    private String businessLevel4;
	
	@Column(name = "business_level_5")
    private String businessLevel5;
	
	@Column(name = "business_level_6")
    private String businessLevel6;
	
	@Column(name = "business_level_7")
    private String businessLevel7;
	
	@Column(name = "claim_id")
    private String claimId;
	
	@Column(name = "claim_source")
    private String claimSource;
	
	@Column(name = "claim_status")
    private String claimStatus;
	
	@Column(name = "claim_type")
    private String claimType;
	
	@Column(name = "contract_id")
    private String contractId;
	
	@Column(name = "group_number")
    private String groupNumber;
  //  private XMLGregorianCalendar memberDOB;
	
	@Column(name = "member_first_name")
    private String memberFirstName;
	
	@Column(name = "member_id")
    private String memberID;

	@Column(name = "member_last_name")
	private String memberLastName;
	
	@Column(name = "paid_ts")
    private Timestamp paidTs;
	
	@Column(name = "person_id")
    private String personId;
	
	@Column(name = "provider_id")
    private String providerId;
	
	@Column(name = "received_ts")
    private Timestamp receivedTs;
	
	@Column(name = "resolved_ts")
    private Timestamp resolvedTs;
//    private List<ServiceLine> serviceLines; // TODO : store in saperate child table
	@Column(name = "services_number")
    private String servicesNumber;
	
	@Column(name = "creation_ts")
	@CreationTimestamp
	private Date crationTs;

	@Column(name = "last_maint_ts")
	@UpdateTimestamp
	private Date lastMaintTs;
	
	@Column(name = "operator_id")
	private String operatorId;

}
