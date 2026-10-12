package com.bank.accountservice.domain.exception;

import com.bank.accountservice.domain.model.AccountId;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(AccountId id, BigDecimal requested) {
        super("Insufficient funds on account " + id.value() + " for amount " + requested);
    }
}
