package com.deltadental.pcp.calculation.entities;

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
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Transactional
@Table(name = PCPCalculationActivityEntity.TABLE_NAME,  schema = "dbo")
@EnableJpaAuditing
@org.hibernate.annotations.Entity(
        dynamicUpdate = true
)
public class PCPCalculationActivityEntity implements java.io.Serializable{
	
	/**
	 * Serialization Key
	 */
	private static final long serialVersionUID = -6512137487572476886L;

	protected static final String TABLE_NAME = "pcp_calculation_activity";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Integer id;

	@NonNull
	@Column(name = "instance_Wise_Data_Load")
	private String instanceId;
	
	@NonNull
	@Column(name = "num_Of_records")
	private Integer numOfRecords;
	
	@Column(name = "time_to_process")
	private long timeToProcess;
	
	@Column(name = "start_time")
	private Timestamp startTime;
	
	@Column(name="end_time")
	private Timestamp endTime;
	
	@Column(name= "created_date", updatable = false)
//	@CreationTimestamp
	private Timestamp createdDate;

	@Column(name = "last_updated_date")
//	@UpdateTimestamp
	private Timestamp lastUpdatedDate;

	@PrePersist
    public void onInsert() {
		createdDate = Timestamp.from(ZonedDateTime.now(ZoneId.of("America/Los_Angeles")).toInstant());
		lastUpdatedDate = createdDate;
    }

    @PreUpdate
    public void onUpdate() {
    	lastUpdatedDate = Timestamp.from(ZonedDateTime.now(ZoneId.of("America/Los_Angeles")).toInstant());
    }
}
