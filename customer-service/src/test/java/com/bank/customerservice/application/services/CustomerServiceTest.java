package com.bank.customerservice.application.services;

import com.bank.customerservice.application.ports.inbound.CustomerUseCase.CustomerDetails;
import com.bank.customerservice.application.ports.inbound.CustomerUseCase.CustomerPatchDetails;
import com.bank.customerservice.application.ports.inbound.CustomerUseCase.CustomerPutDetails;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        assertThat(customers)
                .isNotNull()
                .hasSize(2)
                .containsExactly(customer1, customer2);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findCustomerById() {
        when(customerRepository.findById(customerId1)).thenReturn(Optional.of(customer1));

        Customer foundCustomer = customerService.findById(customerId1);

        assertThat(foundCustomer).isNotNull();
        assertThat(foundCustomer.firstName()).isEqualTo("John");
        verify(customerRepository, times(1)).findById(customerId1);
    }

    @Test
    void findCustomerByIdNotFound() {
        when(customerRepository.findById(customerId1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(customerId1))
                .isInstanceOf(CustomerNotFoundException.class);
        verify(customerRepository, times(1)).findById(customerId1);
    }

    @Test
    void saveCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer1);

        CustomerDetails details = new CustomerDetails("John", "Doe", "john.doe@example.com", "123-456-7890");
        Customer savedCustomer = customerService.create(details);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.firstName()).isEqualTo("John");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomerDetails() {
        CustomerPutDetails updateDetails = new CustomerPutDetails("Johnny", "Doe", "111-222-3333");
        Customer updatedCustomer = new Customer(customerId1, "Johnny", "Doe", "john.doe@example.com", "111-222-3333");

        when(customerRepository.findById(customerId1)).thenReturn(Optional.of(customer1));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        Customer result = customerService.update(customerId1, updateDetails);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Johnny");
        assertThat(result.email()).isEqualTo("john.doe@example.com"); // Email remains unmodifiable
        verify(customerRepository, times(1)).findById(customerId1);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomerDetailsNotFound() {
        CustomerPutDetails updateDetails = new CustomerPutDetails("Johnny", "Doe", "111-222-3333");

        when(customerRepository.findById(customerId1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(customerId1, updateDetails))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining(customerId1.toString());

        verify(customerRepository, times(1)).findById(customerId1);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void patchCustomerDetailsOnlyFirstName() {
        CustomerPatchDetails patchDetails = new CustomerPatchDetails("Johnny", null, null);
        Customer patchedCustomer = new Customer(customerId1, "Johnny", "Doe", "john.doe@example.com", "123-456-7890");

        when(customerRepository.findById(customerId1)).thenReturn(Optional.of(customer1));
        when(customerRepository.save(any(Customer.class))).thenReturn(patchedCustomer);

        Customer result = customerService.patch(customerId1, patchDetails);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Johnny");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.email()).isEqualTo("john.doe@example.com");
        assertThat(result.phoneNumber()).isEqualTo("123-456-7890");
        verify(customerRepository, times(1)).findById(customerId1);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void deleteCustomerById() {
        doNothing().when(customerRepository).deleteById(customerId1);

        customerService.delete(customerId1);

        verify(customerRepository, times(1)).deleteById(customerId1);
    }
}
