package com.deltadental.pcp.calculation.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Transactional
@EnableJpaAuditing
@Table(name = "pcp_calculation_activity", schema = "dbo")
public class PCPCalculationActivityEntity implements java.io.Serializable {

    private static final long serialVersionUID = -6512137487572476886L;

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @NotNull
    @Column(name = "instance_wise_data_load")
    private String instanceId;

    @NotNull
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
