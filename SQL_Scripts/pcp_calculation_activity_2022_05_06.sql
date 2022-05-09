
CREATE TABLE dbo.pcp_calculation_activity (
	id varchar(200) not null,
	start_time datetime NULL,
    end_time datetime NULL,
	instance_wise_data_load varchar(255) ,
	num_of_records int NULL,
	time_to_process bigint NULL,
    created_at datetime NULL,
    last_updated_at datetime NULL,
	CONSTRAINT pk_pcp_calculation_activity_id PRIMARY KEY (id)
);