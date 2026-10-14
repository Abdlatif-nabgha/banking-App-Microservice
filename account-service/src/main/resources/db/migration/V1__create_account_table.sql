CREATE TABLE account (
    id             UUID           PRIMARY KEY,
    customer_id    UUID           NOT NULL,
    account_number VARCHAR(20)    NOT NULL UNIQUE,
    account_type   VARCHAR(20)    NOT NULL,
    status         VARCHAR(20)    NOT NULL,
    balance        NUMERIC(19, 2) NOT NULL DEFAULT 0,
    version        BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_account_customer_id ON account (customer_id);
