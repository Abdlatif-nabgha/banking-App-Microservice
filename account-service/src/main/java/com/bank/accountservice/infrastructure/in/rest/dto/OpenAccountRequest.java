package com.bank.accountservice.infrastructure.in.rest.dto;

import com.bank.accountservice.application.ports.inbound.OpenAccountCommand;
import com.bank.accountservice.domain.model.AccountType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OpenAccountRequest(
        @NotNull UUID customerId,
        @NotNull AccountType accountType
) {
    public OpenAccountCommand toCommand() {
        return new OpenAccountCommand(customerId, accountType);
    }
}