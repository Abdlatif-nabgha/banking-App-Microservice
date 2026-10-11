package com.bank.customerservice.application.ports.outbound;


import com.bank.customerservice.domain.model.Customer;
import com.bank.customerservice.domain.model.CustomerId;

import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {
    List<Customer> findAll();
    Optional<Customer> findById(CustomerId id);
    Customer save(Customer customer);
    void deleteById(CustomerId id);
}
