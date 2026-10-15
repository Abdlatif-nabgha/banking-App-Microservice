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


    record CustomerPutDetails(
            String firstName,
            String lastName,
            String phoneNumber
    ) {}

    record CustomerPatchDetails(
            String firstName,
            String lastName,
            String phoneNumber
    ) {}


    List<Customer> findAll();
    Customer findById(CustomerId id);
    Customer create(CustomerDetails details);
    Customer update(CustomerId id, CustomerPutDetails customer);
    Customer patch(CustomerId id, CustomerPatchDetails customer);
    void delete(CustomerId id);
}
