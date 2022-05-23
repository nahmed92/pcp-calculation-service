
DROP TABLE CREATE TABLE [dbo].[contract_member_claim];

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
	creation_at datetime DEFAULT getdate() NULL,
	last_updated_at datetime DEFAULT getdate() NULL,
	instance_id varchar(25) NULL,
	error_message varchar(4096) NULL,
	CONSTRAINT uq_claim_contract_member_claim_provider_state UNIQUE  (claim_id,contract_id,member_id,provider_id,state)
);

ALTER TABLE contract_member_claim ADD CONSTRAINT pk_contract_member_claim_id_sequence_id PRIMARY KEY (id,sequence_id)

CREATE  INDEX [idx_instance_id_1] ON [dbo].[contract_member_claim](instance_id);
