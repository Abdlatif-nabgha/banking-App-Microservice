package com.bank.customerservice.application.port.in;


import com.bank.customerservice.domain.Customer;

import java.util.List;

public interface CustomerUseCase {

    record CustomerDetails(
            String firstName,
            String lastName,
            String email,
            String phoneNumber
    ) {}

    List<Customer> findAll();
    Customer findById(Long id);
    Customer create(CustomerDetails details);
    Customer update(Long id, CustomerDetails customer);
    void delete(Long id);
}
