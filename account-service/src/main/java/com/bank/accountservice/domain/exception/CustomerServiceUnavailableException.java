package com.bank.accountservice.domain.exception;


public class CustomerServiceUnavailableException extends RuntimeException {
    public CustomerServiceUnavailableException() {
        super("Customer verification is temporarily unavailable, please retry later");
    }
}
