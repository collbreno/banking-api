BEGIN;

CREATE TABLE accounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    document VARCHAR(11) NOT NULL UNIQUE,
    CONSTRAINT chk_accounts_document_length CHECK (char_length(document) = 11)
);

CREATE TABLE transactions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    operation_code SMALLINT NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount NUMERIC(19, 2) NOT NULL,
    account_id BIGINT NOT NULL
        REFERENCES accounts (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_transactions_operation_code CHECK (
        operation_code IN (1, 2, 3, 4)
    )
);

CREATE INDEX idx_transactions_account_id
    ON transactions (account_id);

COMMIT;
