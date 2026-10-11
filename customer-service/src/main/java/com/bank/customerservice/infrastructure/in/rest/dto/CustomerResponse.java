package com.bank.customerservice.infrastructure.in.rest.dto;

import com.bank.customerservice.domain.model.Customer;

public record CustomerResponse(Long id, String firstName, String lastName, String email, String phoneNumber) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.id(),
                customer.firstName(),
                customer.lastName(),
                customer.email(),
                customer.phoneNumber()
        );
    }
}
