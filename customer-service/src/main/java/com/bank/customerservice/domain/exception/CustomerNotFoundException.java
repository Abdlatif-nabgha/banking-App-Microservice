package com.bank.customerservice.domain.exception;


public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long message) {
        super("Customer not found with id: " + message);
    }
}
