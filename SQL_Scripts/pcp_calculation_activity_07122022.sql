
EXEC sp_rename 'pcp_calculation_activity', 'pcp_calculation_activity_07122022';

CREATE TABLE dbo.pcp_calculation_activity (
	id varchar(200) NOT NULL,
	created_date datetime2 NULL,
	end_time datetime2 NULL,
	instance_wise_data_load varchar(255) NULL,
	last_updated_date datetime2 NULL,
	num_of_records int NULL,
	start_time datetime2 NULL,
	time_to_process bigint NULL,
	created_at datetime2 NULL,
	last_updated_at datetime2 NULL,
	CONSTRAINT PK__pcp_calculation_activity PRIMARY KEY (id)
);