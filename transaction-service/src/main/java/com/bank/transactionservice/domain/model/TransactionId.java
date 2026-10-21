package com.bank.transactionservice.domain.model;

import java.util.Objects;
import java.util.UUID;

public record TransactionId(UUID value) {
    public TransactionId {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId of(UUID id) {
        return new TransactionId(id);
    }

    public static TransactionId of(String id) {
        return new TransactionId(UUID.fromString(id));
    }
}
