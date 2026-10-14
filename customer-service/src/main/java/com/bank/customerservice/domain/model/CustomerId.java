package com.bank.customerservice.domain.model;


import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {
    public CustomerId {
        Objects.requireNonNull(value, "CustomerId value cannot be null");
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }
    public static CustomerId of(String id) {
        return new CustomerId(UUID.fromString(id));
    }
    public static CustomerId of(UUID id) {
        return new CustomerId(id);
    }

}
