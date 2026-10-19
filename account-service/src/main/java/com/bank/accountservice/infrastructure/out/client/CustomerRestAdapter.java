package com.bank.accountservice.infrastructure.out.client;


import com.bank.accountservice.application.ports.outbound.CustomerVerificationPort;
import com.bank.accountservice.domain.exception.CustomerServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerRestAdapter implements CustomerVerificationPort {

    private final CustomerFeignClient customerClient;

    @Override
    @CircuitBreaker(name = "customer-service", fallbackMethod = "customerExistsFallback")
    public boolean customerExists(UUID customerId) {
        try {
            customerClient.getCustomer(customerId);
            return true;
        } catch (FeignException.NotFound e) {
            return false;
        }
    }

    private boolean customerExistsFallback(UUID customerId, Throwable ex) {
        log.warn("customer-service unavailable ({}): {}", ex.getClass().getSimpleName(), ex.getMessage());
        throw new CustomerServiceUnavailableException();
    }
}
