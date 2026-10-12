package com.bank.accountservice.infrastructure.out.client;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRestAdapterTest {

    @Mock
    private CustomerFeignClient customerFeignClient;

    private CustomerRestAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CustomerRestAdapter(customerFeignClient);
    }

    @Test
    @DisplayName("Should return true when customer exists")
    void shouldReturnTrueWhenCustomerExists() {
        UUID customerId = UUID.randomUUID();
        when(customerFeignClient.getCustomer(customerId)).thenReturn(new CustomerClientResponse(customerId));

        boolean exists = adapter.customerExists(customerId);

        assertThat(exists).isTrue();
        verify(customerFeignClient).getCustomer(customerId);
    }

    @Test
    @DisplayName("Should return false when Feign throws 404 NotFound")
    void shouldReturnFalseWhenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(customerFeignClient.getCustomer(customerId)).thenThrow(notFoundException);

        boolean exists = adapter.customerExists(customerId);

        assertThat(exists).isFalse();
        verify(customerFeignClient).getCustomer(customerId);
    }

    @Test
    @DisplayName("Should rethrow FeignException when customer-service fails with 500 or is down")
    void shouldRethrowExceptionWhenServiceIsDownOrError() {
        UUID customerId = UUID.randomUUID();
        FeignException.InternalServerError serverError = mock(FeignException.InternalServerError.class);
        when(customerFeignClient.getCustomer(customerId)).thenThrow(serverError);

        assertThatThrownBy(() -> adapter.customerExists(customerId))
                .isInstanceOf(FeignException.class);
    }
}
