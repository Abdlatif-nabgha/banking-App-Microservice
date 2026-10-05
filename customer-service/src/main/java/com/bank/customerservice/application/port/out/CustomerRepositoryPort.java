package com.bank.customerservice.application.port.out;


import com.bank.customerservice.domain.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {
    List<Customer> findAll();
    Optional<Customer> findById(Long id);
    Customer save(Customer customer);
    void deleteById(Long id);
}
