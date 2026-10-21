package com.bank.transactionservice.domain.exception;

public class AccountServiceUnavailableException extends RuntimeException {
    public AccountServiceUnavailableException() {
        super("Account service is temporarily unavailable");
    }
}