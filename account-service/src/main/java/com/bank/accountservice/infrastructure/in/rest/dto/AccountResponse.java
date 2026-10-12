package com.bank.accountservice.infrastructure.in.rest.dto;

import com.bank.accountservice.domain.model.Account;
import com.bank.accountservice.domain.model.AccountStatus;
import com.bank.accountservice.domain.model.AccountType;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(UUID id, UUID customerId, String accountNumber,
                              AccountType accountType, BigDecimal balance,
                              AccountStatus status) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId().value(),
                account.getCustomerId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus());
    }
}