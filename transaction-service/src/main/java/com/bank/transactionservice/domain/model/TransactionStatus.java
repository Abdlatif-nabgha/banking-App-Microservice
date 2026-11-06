package com.bank.transactionservice.domain.model;


public enum TransactionStatus {
    PENDING,
    SOURCE_DEBITED,
    COMPLETED,
    COMPENSATING,
    COMPENSATED,
    FAILED
}
