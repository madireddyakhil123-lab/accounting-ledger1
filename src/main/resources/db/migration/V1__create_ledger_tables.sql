
CREATE TABLE accounts (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          tenant_id VARCHAR(100) NOT NULL,
                          account_code VARCHAR(50) NOT NULL,
                          account_name VARCHAR(150) NOT NULL,

                          PRIMARY KEY (id),
                          UNIQUE KEY uk_account_tenant_id (tenant_id, id),
                          UNIQUE KEY uk_account_code (tenant_id, account_code)
) ENGINE=InnoDB;

CREATE TABLE journal_entries (
                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                 tenant_id VARCHAR(100) NOT NULL,
                                 entry_date DATE NOT NULL,
                                 description VARCHAR(500),
                                 idempotency_key VARCHAR(100) NOT NULL,
                                 reversal_of_id BIGINT,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 PRIMARY KEY (id),
                                 UNIQUE KEY uk_entry_tenant_id (tenant_id, id),
                                 UNIQUE KEY uk_entry_idempotency (tenant_id, idempotency_key),
                                 UNIQUE KEY uk_entry_reversal (tenant_id, reversal_of_id),

                                 CONSTRAINT fk_reversal_original
                                     FOREIGN KEY (tenant_id, reversal_of_id)
                                         REFERENCES journal_entries (tenant_id, id)
) ENGINE=InnoDB;

CREATE TABLE journal_entry_lines (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     tenant_id VARCHAR(100) NOT NULL,
                                     journal_entry_id BIGINT NOT NULL,
                                     account_id BIGINT NOT NULL,
                                     debit_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
                                     credit_amount DECIMAL(19,4) NOT NULL DEFAULT 0,

                                     PRIMARY KEY (id),

                                     CONSTRAINT chk_line_amounts CHECK (
                                         debit_amount >= 0
                                             AND credit_amount >= 0
                                             AND (
                                             (debit_amount > 0 AND credit_amount = 0)
                                                 OR
                                             (credit_amount > 0 AND debit_amount = 0)
                                             )
                                         ),

                                     CONSTRAINT fk_line_entry
                                         FOREIGN KEY (tenant_id, journal_entry_id)
                                             REFERENCES journal_entries (tenant_id, id),

                                     CONSTRAINT fk_line_account
                                         FOREIGN KEY (tenant_id, account_id)
                                             REFERENCES accounts (tenant_id, id),

                                     INDEX idx_lines_account (tenant_id, account_id)

) ENGINE=InnoDB;
