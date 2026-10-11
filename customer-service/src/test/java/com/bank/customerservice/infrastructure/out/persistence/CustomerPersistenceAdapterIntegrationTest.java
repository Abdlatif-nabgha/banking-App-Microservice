package com.bank.customerservice.infrastructure.out.persistence;

import com.bank.customerservice.domain.model.Customer;
import com.bank.customerservice.domain.model.CustomerId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CustomerPersistenceAdapter.class)
public class CustomerPersistenceAdapterIntegrationTest {

    @Autowired
    private CustomerPersistenceAdapter adapter;

    @Test
    void whenSaveCustomerViaAdapter_thenCanFindById() {
        CustomerId id = CustomerId.generate();
        Customer customer = new Customer(
                id,
                "Alice",
                "Wonderland",
                "alice." + UUID.randomUUID() + "@example.com",
                "111-222-3333"
        );

        Customer saved = adapter.save(customer);

        assertNotNull(saved);
        assertEquals(id, saved.id());

        Optional<Customer> found = adapter.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().firstName());
        assertEquals("Wonderland", found.get().lastName());
    }

    @Test
    void whenDeleteCustomerViaAdapter_thenCannotFindById() {
        CustomerId id = CustomerId.generate();
        Customer customer = new Customer(
                id,
                "Bob",
                "Builder",
                "bob." + UUID.randomUUID() + "@example.com",
                "444-555-6666"
        );

        adapter.save(customer);
        assertTrue(adapter.findById(id).isPresent());

        adapter.deleteById(id);
        assertFalse(adapter.findById(id).isPresent());
    }
}
