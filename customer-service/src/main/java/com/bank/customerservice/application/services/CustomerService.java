package com.bank.customerservice.application.services;

import com.bank.customerservice.application.ports.inbound.CustomerUseCase;
import com.bank.customerservice.application.ports.outbound.CustomerRepositoryPort;
import com.bank.customerservice.domain.model.Customer;
import com.bank.customerservice.domain.exception.CustomerNotFoundException;
import com.bank.customerservice.domain.model.CustomerId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerService implements CustomerUseCase {

    private final CustomerRepositoryPort customers;

    @Override
    public List<Customer> findAll() {
        return customers.findAll();
    }

    @Override
    public Customer findById(CustomerId id) {
        return customers.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Override
    @Transactional
    public Customer create(CustomerDetails d) {
        return customers.save(
                new Customer(CustomerId.generate(), d.firstName(), d.lastName(), d.email(), d.phoneNumber())
        );
    }

    @Override
    @Transactional
    public Customer update(CustomerId id, CustomerDetails d) {
        Customer customer = findById(id);
        return customers.save(customer.withDetails(d.firstName(), d.lastName(), d.email(), d.phoneNumber()));
    }

    @Override
    @Transactional
    public void delete(CustomerId id) {
        customers.deleteById(id);
    }
}
