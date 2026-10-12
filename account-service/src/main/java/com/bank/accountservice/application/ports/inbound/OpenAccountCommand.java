package com.bank.accountservice.application.ports.inbound;

import com.bank.accountservice.domain.model.AccountType;

import java.util.UUID;

public record OpenAccountCommand(UUID customerId, AccountType accountType) {}
