-- CREATE EXTENSION --
CREATE EXTENSION pgcrypto;

-- CREATE SCHEMA --
CREATE SCHEMA basapp;

-- SET DEFAULT SCHEMA --
SET search_path TO basapp;

-- SET TIME ZONE
SET TIME ZONE 'Europe/Paris';


-- CREATE TABLES --

-- CREATE customer TABLE --
	CREATE TABLE basapp.customers
	(
	customer_id BIGSERIAL UNIQUE NOT NULL,
	first_name VARCHAR(750) NOT NULL,
	last_name VARCHAR(750) NOT NULL,
	gender CHAR(1) NOT NULL,
	marital_status CHAR(1) NOT NULL,
	birthday DATE NOT NULL,
	country VARCHAR(255) NOT NULL,
	address text NOT NULL,
	city VARCHAR(255) NOT NULL,
	email VARCHAR(255) UNIQUE NOT NULL,
	phone_number VARCHAR(20) NOT NULL,
	metadata jsonb,
	CONSTRAINT pk_customer PRIMARY KEY (customer_id)
	);

-- CREATE bank_account TABLE --
	CREATE TABLE basapp.bank_accounts
	(
	account_id BIGSERIAL UNIQUE NOT NULL,
	type varchar(25) NOT NULL,
	balance DECIMAL NOT NULL,
	CONSTRAINT pk_bank_account PRIMARY KEY (account_id)
	);

-- CREATE transaction TABLE --
	CREATE TABLE basapp.transactions
	(
	transaction_id BIGSERIAL UNIQUE NOT NULL,
    emitter_account_id bigint NOT NULL,
    receiver_account_id bigint,
	type VARCHAR(25) NOT NULL,
	amount DECIMAL NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(25) NOT NULL,
	date TIMESTAMP WITH TIME ZONE NOT NULL,
	label text NOT NULL,
	metadata jsonb,
	CONSTRAINT pk_transaction PRIMARY KEY (transaction_id),
    CONSTRAINT fk_emitter_account_id FOREIGN KEY(emitter_account_id) REFERENCES basapp.bank_accounts(account_id),
    CONSTRAINT fk_receiver_account_id FOREIGN KEY(receiver_account_id) REFERENCES basapp.bank_accounts(account_id)
	);
	
	-- CREATE bank_account_customer TABLE --
	CREATE TABLE basapp.bank_accounts_customers
	(
	account_id bigint NOT NULL,
	customer_id bigint NOT NULL,
	PRIMARY KEY (account_id, customer_id),
	CONSTRAINT fk_account_id FOREIGN KEY(account_id) REFERENCES basapp.bank_accounts(account_id),
	CONSTRAINT fk_customer_id FOREIGN KEY(customer_id) REFERENCES basapp.customers(customer_id)
	);

-- GRANT USER --
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA basapp TO basadm;
GRANT SELECT, UPDATE, USAGE ON ALL SEQUENCES IN SCHEMA basapp to basadm;

CREATE SEQUENCE IF NOT EXISTS basapp.hibernate_sequence;