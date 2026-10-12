package com.bank.accountservice.domain.model;


import java.util.Objects;
import java.util.UUID;

public record AccountId(UUID value) {
    public AccountId {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }

    public static AccountId of(String id) {
        return new AccountId(UUID.fromString(id));
    }
    public static AccountId of(UUID id) {
        return new AccountId(id);
    }
}
