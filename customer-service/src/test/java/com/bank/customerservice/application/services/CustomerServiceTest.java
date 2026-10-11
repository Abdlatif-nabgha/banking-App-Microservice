package com.bank.customerservice.application.services;

import com.bank.customerservice.application.ports.inbound.CustomerUseCase.CustomerDetails;
import com.bank.customerservice.application.ports.outbound.CustomerRepositoryPort;
import com.bank.customerservice.domain.exception.CustomerNotFoundException;
import com.bank.customerservice.domain.model.Customer;
import com.bank.customerservice.domain.model.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerId customerId1;
    private CustomerId customerId2;
    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        customerId1 = CustomerId.generate();
        customerId2 = CustomerId.generate();

        customer1 = new Customer(customerId1, "John", "Doe", "john.doe@example.com", "123-456-7890");
        customer2 = new Customer(customerId2, "Jane", "Smith", "jane.smith@example.com", "098-765-4321");
    }

    @Test
    void findAllCustomers() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));

        List<Customer> customers = customerService.findAll();

        assertNotNull(customers);
        assertEquals(2, customers.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findCustomerById() {
        when(customerRepository.findById(customerId1)).thenReturn(Optional.of(customer1));

        Customer foundCustomer = customerService.findById(customerId1);

        assertNotNull(foundCustomer);
        assertEquals("John", foundCustomer.firstName());
        verify(customerRepository, times(1)).findById(customerId1);
    }

    @Test
    void findCustomerByIdNotFound() {
        when(customerRepository.findById(customerId1)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.findById(customerId1));
        verify(customerRepository, times(1)).findById(customerId1);
    }

    @Test
    void saveCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer1);

        CustomerDetails details = new CustomerDetails("John", "Doe", "john.doe@example.com", "123-456-7890");
        Customer savedCustomer = customerService.create(details);

        assertNotNull(savedCustomer);
        assertEquals("John", savedCustomer.firstName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomerDetails() {
        CustomerDetails updateDetails = new CustomerDetails("Johnny", "Doe", "johnny.doe@example.com", "111-222-3333");
        Customer updatedCustomer = new Customer(customerId1, "Johnny", "Doe", "johnny.doe@example.com", "111-222-3333");

        when(customerRepository.findById(customerId1)).thenReturn(Optional.of(customer1));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        Customer result = customerService.update(customerId1, updateDetails);

        assertNotNull(result);
        assertEquals("Johnny", result.firstName());
        assertEquals("johnny.doe@example.com", result.email());
        verify(customerRepository, times(1)).findById(customerId1);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomerDetailsNotFound() {
        CustomerDetails updateDetails = new CustomerDetails("Johnny", "Doe", "johnny.doe@example.com", "111-222-3333");

        when(customerRepository.findById(customerId1)).thenReturn(Optional.empty());

        CustomerNotFoundException thrown = assertThrows(CustomerNotFoundException.class, () ->
                customerService.update(customerId1, updateDetails)
        );

        assertTrue(thrown.getMessage().contains(customerId1.toString()));
        verify(customerRepository, times(1)).findById(customerId1);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomerById() {
        doNothing().when(customerRepository).deleteById(customerId1);

        customerService.delete(customerId1);

        verify(customerRepository, times(1)).deleteById(customerId1);
    }
}
