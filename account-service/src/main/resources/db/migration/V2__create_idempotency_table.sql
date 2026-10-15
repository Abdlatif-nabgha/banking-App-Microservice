CREATE TABLE idempotency_records (
     key           VARCHAR(255) PRIMARY KEY,
     request_hash  VARCHAR(64)  NOT NULL,
     status        VARCHAR(20)  NOT NULL,
     response_body TEXT,
     created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
