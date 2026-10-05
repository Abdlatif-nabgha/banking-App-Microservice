package com.bank.customerservice.application;

import com.bank.customerservice.application.port.in.CustomerUseCase;
import com.bank.customerservice.application.port.out.CustomerRepositoryPort;
import com.bank.customerservice.domain.Customer;
import com.bank.customerservice.domain.CustomerNotFoundException;
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
    public Customer findById(Long id) {
        return customers.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Override
    @Transactional
    public Customer create(CustomerDetails d) {
        return customers.save(
                new Customer(null, d.firstName(), d.lastName(), d.email(), d.phoneNumber())
        );
    }

    @Override
    @Transactional
    public Customer update(Long id, CustomerDetails d) {
        Customer customer = findById(id);
        return customers.save(customer.withDetails(d.firstName(), d.lastName(), d.email(), d.phoneNumber()));
    }

    @Override
    public void delete(Long id) {
        customers.deleteById(id);
    }
}
