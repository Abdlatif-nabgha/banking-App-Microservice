CREATE TABLE transaction
(
    id                     UUID           PRIMARY KEY,
    type                   VARCHAR(20)    NOT NULL,
    source_account_id      UUID,
    destination_account_id UUID,
    amount                 NUMERIC(19, 2) NOT NULL,
    status                 VARCHAR(20)    NOT NULL,
    failure_reason         TEXT,
    created_at             TIMESTAMPTZ    NOT NULL DEFAULT now(),
    version                BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_transaction_source      ON transaction (source_account_id);
CREATE INDEX idx_transaction_destination ON transaction (destination_account_id);
CREATE INDEX idx_transaction_created_at  ON transaction (created_at DESC);
