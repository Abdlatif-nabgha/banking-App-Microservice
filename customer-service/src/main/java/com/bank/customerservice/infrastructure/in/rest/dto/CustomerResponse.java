package com.bank.customerservice.infrastructure.in.rest.dto;

import com.bank.customerservice.domain.model.Customer;

import java.util.UUID;

public record CustomerResponse(UUID id, String firstName, String lastName, String email, String phoneNumber) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.id() != null ? customer.id().value() : null,
                customer.firstName(),
                customer.lastName(),
                customer.email(),
                customer.phoneNumber()
        );
    }
}
