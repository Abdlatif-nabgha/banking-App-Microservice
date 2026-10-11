package com.bank.customerservice.infrastructure.out.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerRepositoryIntegrationTest {

    @Autowired
    private CustomerJpaRepository customerJpaRepository;

    @Test
    void whenSaveCustomer_thenCanFindById() {
        CustomerJpaEntity customer = new CustomerJpaEntity();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Alice");
        customer.setLastName("Wonderland");
        customer.setEmail("alice." + UUID.randomUUID() + "@example.com");
        customer.setPhoneNumber("111-222-3333");

        CustomerJpaEntity savedCustomer = customerJpaRepository.save(customer);

        assertNotNull(savedCustomer.getId());
        Optional<CustomerJpaEntity> foundCustomer = customerJpaRepository.findById(savedCustomer.getId());
        assertTrue(foundCustomer.isPresent());
        assertEquals("Alice", foundCustomer.get().getFirstName());
        assertEquals("Wonderland", foundCustomer.get().getLastName());
    }

    @Test
    void whenDeleteCustomer_thenCannotFindById() {
        CustomerJpaEntity customer = new CustomerJpaEntity();
        UUID customerId = UUID.randomUUID();
        customer.setId(customerId);
        customer.setFirstName("Bob");
        customer.setLastName("Builder");
        customer.setEmail("bob." + customerId + "@example.com");
        customer.setPhoneNumber("444-555-6666");

        customerJpaRepository.save(customer);
        assertTrue(customerJpaRepository.findById(customerId).isPresent());

        customerJpaRepository.deleteById(customerId);
        assertFalse(customerJpaRepository.findById(customerId).isPresent());
    }
}
