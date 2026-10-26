package com.bank.transactionservice.infrastructure.adapter.in.rest.dto;


import com.bank.transactionservice.domain.model.Transaction;
import com.bank.transactionservice.domain.model.TransactionStatus;
import com.bank.transactionservice.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TransactionType type,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        TransactionStatus status,
        String failureReason,
        Instant createdAt
) {

    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
                tx.getId().value(),
                tx.getType(),
                tx.getSourceAccountId(),
                tx.getDestinationAccountId(),
                tx.getAmount(),
                tx.getStatus(),
                tx.getFailureReason(),
                tx.getCreatedAt()
        );
    }
}
