package com.bank.customerservice.application.ports.inbound;

import com.bank.customerservice.domain.model.Customer;
import com.bank.customerservice.domain.model.CustomerId;

import java.util.List;

public interface CustomerUseCase {

    record CustomerDetails(
            String firstName,
            String lastName,
            String email,
            String phoneNumber
    ) {}

    List<Customer> findAll();
    Customer findById(CustomerId id);
    Customer create(CustomerDetails details);
    Customer update(CustomerId id, CustomerDetails customer);
    void delete(CustomerId id);
}
