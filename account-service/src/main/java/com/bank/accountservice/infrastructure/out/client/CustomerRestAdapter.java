package com.bank.accountservice.infrastructure.out.client;


import com.bank.accountservice.application.ports.outbound.CustomerInfo;
import com.bank.accountservice.application.ports.outbound.CustomerVerificationPort;
import com.bank.accountservice.domain.exception.CustomerServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerRestAdapter implements CustomerVerificationPort {

    private final CustomerFeignClient customerClient;

    @Override
    @CircuitBreaker(name = "customer-service", fallbackMethod = "findCustomerFallback")
    public Optional<CustomerInfo> findCustomer(UUID customerId) {
        try {
            CustomerClientResponse response = customerClient.getCustomer(customerId);
            return Optional.of(new CustomerInfo(response.id(), response.firstName(), response.lastName(), response.email()));
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        }
    }

    private Optional<CustomerInfo> findCustomerFallback(UUID customerId, Throwable ex) {
        log.warn("customer-service unavailable ({}): {}", ex.getClass().getSimpleName(), ex.getMessage());
        throw new CustomerServiceUnavailableException();
    }
}
