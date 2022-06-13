
<<<<<<< HEAD
EXEC sp_rename 'contract_member_claim', 'contract_member_claim_13062022';
=======
>>>>>>> 82dc974d9bc318d520c119bcb236fa4a3e1b162a

CREATE TABLE [dbo].[contract_member_claim] (
	id varchar(200) NOT NULL,
	sequence_id smallint NOT NULL,
	claim_id varchar(25) NOT NULL,
	contract_id varchar(15) NOT NULL,
	member_id varchar(2) NOT NULL,
	provider_id varchar(15) NOT NULL,
	state varchar(2) NOT NULL,
	status varchar(15) NULL,
	operator_id varchar(25) NULL,
	created_at datetime DEFAULT getdate() NULL,
	last_updated_at datetime DEFAULT getdate() NULL,
	instance_id varchar(25) NULL,
	error_message varchar(4096) NULL
);

ALTER TABLE contract_member_claim ADD CONSTRAINT pk_contract_member_claim_id_sequence_id PRIMARY KEY (id,sequence_id)

ALTER TABLE [dbo].[member_claim_service] ADD [from_date] date;

ALTER TABLE [dbo].[member_claim_service] ADD [thru_date] date;

ALTER TABLE [dbo].[member_claim_service] ADD [received_timestamp] datetime;	

CREATE  INDEX [idx_instance_id_1] ON [dbo].[contract_member_claim](instance_id);

CREATE  INDEX [idx_instance_id_2] ON [dbo].[contract_member_claim](claim_id, contract_id, provider_id,member_id, state);

