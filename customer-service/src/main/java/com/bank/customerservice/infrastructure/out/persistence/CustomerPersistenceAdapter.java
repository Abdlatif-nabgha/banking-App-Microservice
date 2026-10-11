package com.bank.customerservice.infrastructure.out.persistence;


import com.bank.customerservice.application.ports.outbound.CustomerRepositoryPort;
import com.bank.customerservice.domain.model.Customer;
import com.bank.customerservice.domain.model.CustomerId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository jpa;

    @Override
    public List<Customer> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return jpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        return toDomain(jpa.save(toEntity(customer)));
    }

    private CustomerJpaEntity toEntity(Customer customer) {
        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.setId(customer.id() != null ? customer.id().value() : null);
        entity.setFirstName(customer.firstName());
        entity.setLastName(customer.lastName());
        entity.setEmail(customer.email());
        entity.setPhoneNumber(customer.phoneNumber());
        return entity;
    }

    private Customer toDomain(CustomerJpaEntity entity) {
        return new Customer(
                CustomerId.of(entity.getId()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhoneNumber()
        );
    }

    @Override
    public void deleteById(CustomerId id) {
        jpa.deleteById(id.value());
    }
}
