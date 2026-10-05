package com.bank.customerservice.adapter.out.persistence;


import com.bank.customerservice.application.port.out.CustomerRepositoryPort;
import com.bank.customerservice.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository jpa;

    @Override
    public List<Customer> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        return toDomain(jpa.save(jpa.save(toEntity(customer))));
    }

    private CustomerJpaEntity toEntity(Customer customer) {
        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.setId(customer.id());
        entity.setFirstName(customer.firstName());
        entity.setLastName(customer.lastName());
        entity.setEmail(customer.email());
        entity.setPhoneNumber(customer.phoneNumber());
        return entity;
    }

    private Customer toDomain(CustomerJpaEntity entity) {
        return new Customer(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getEmail(), entity.getPhoneNumber());
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
