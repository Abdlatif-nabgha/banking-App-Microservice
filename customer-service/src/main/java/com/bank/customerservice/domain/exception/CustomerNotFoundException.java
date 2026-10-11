package com.bank.customerservice.domain.exception;

import com.bank.customerservice.domain.model.CustomerId;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(CustomerId id) {
        super("Customer not found with id: " + id);
    }
}
