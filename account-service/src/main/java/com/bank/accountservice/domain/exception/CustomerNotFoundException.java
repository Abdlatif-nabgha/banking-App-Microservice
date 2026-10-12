package com.bank.accountservice.domain.exception;


import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(UUID customerId) {
        super("Customer with id " + customerId + " not found");
    }
}
