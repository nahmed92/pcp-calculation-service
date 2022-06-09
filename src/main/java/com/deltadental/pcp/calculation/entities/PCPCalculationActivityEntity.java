package com.deltadental.pcp.calculation.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Transactional
@EnableJpaAuditing
@Table(name = "pcp_calculation_activity", schema = "dbo")
public class PCPCalculationActivityEntity implements java.io.Serializable {

	private static final long serialVersionUID = -6512137487572476886L;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	private String id;

	@NonNull
	@Column(name = "instance_wise_data_load")
	private String instanceId;

	@NonNull
	@Column(name = "num_of_records")
	private Integer numOfRecords;

	@Column(name = "time_to_process")
	private long timeToProcess;

	@Column(name = "start_time")
	private Timestamp startTime;

	@Column(name = "end_time")
	private Timestamp endTime;

	@Column(name = "created_at", updatable = false)
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name = "last_updated_at")
	@UpdateTimestamp
	private Timestamp lastUpdatedAt;

}
