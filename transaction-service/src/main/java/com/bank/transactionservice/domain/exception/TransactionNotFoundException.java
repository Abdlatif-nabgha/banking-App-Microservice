package com.bank.transactionservice.domain.exception;

import com.bank.transactionservice.domain.model.TransactionId;


public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(TransactionId id) {
        super("Transaction not found: " + id.value());
    }
}
