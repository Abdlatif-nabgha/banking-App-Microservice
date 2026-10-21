package com.bank.transactionservice.domain.exception;

public class AccountOperationRejectedException extends RuntimeException {
    public AccountOperationRejectedException(String message) { super(message); }
}

