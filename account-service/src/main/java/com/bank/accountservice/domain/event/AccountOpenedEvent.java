package com.bank.accountservice.domain.event;


import com.bank.accountservice.domain.model.AccountType;

import java.time.Instant;
import java.util.UUID;

public record AccountOpenedEvent(
        UUID eventId,
        UUID accountId,
        UUID customerId,
        String customerFirstName,
        String customerLastName,
        String customerEmail,
        String accountNumber,
        AccountType accountType,
        Instant occurredAt
) {}
