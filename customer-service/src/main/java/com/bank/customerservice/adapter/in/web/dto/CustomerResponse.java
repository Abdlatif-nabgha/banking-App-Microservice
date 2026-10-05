package com.bank.customerservice.adapter.in.web.dto;

import com.bank.customerservice.domain.Customer;

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
