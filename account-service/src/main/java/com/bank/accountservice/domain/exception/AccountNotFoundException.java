package com.bank.accountservice.domain.exception;


import com.bank.accountservice.domain.model.AccountId;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(AccountId id) {
        super("Account not found: " + id.value());
    }
}
