package com.bank.transactionservice.infrastructure.adapter.out.client;


import com.bank.transactionservice.application.port.out.AccountPort;
import com.bank.transactionservice.domain.exception.AccountNotFoundException;
import com.bank.transactionservice.domain.exception.AccountOperationRejectedException;
import com.bank.transactionservice.domain.exception.AccountServiceUnavailableException;
import com.bank.transactionservice.domain.exception.InsufficientFundsException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class AccountRestAdapter implements AccountPort {

    private final RestClient accountRestClient;

    @Override
    @CircuitBreaker(name = "account-service", fallbackMethod = "accountServiceFallback")
    public void deposit(UUID accountId, BigDecimal amount) {
        execute(accountId, "/api/v1/accounts/{id}/deposit", amount);
    }

    @Override
    @CircuitBreaker(name = "account-service", fallbackMethod = "accountServiceFallback")
    public void withdraw(UUID accountId, BigDecimal amount) {
        execute(accountId, "/api/v1/accounts/{id}/withdraw", amount);
    }

    private void accountServiceFallback(UUID accountId, BigDecimal amount, Throwable ex) {
        if (ex instanceof AccountNotFoundException notFound) {
            throw notFound;
        }
        if (ex instanceof InsufficientFundsException insufficientFunds) {
            throw insufficientFunds;
        }
        if (ex instanceof AccountOperationRejectedException rejected) {
            throw rejected;
        }
        if (ex instanceof AccountServiceUnavailableException unavailable) {
            throw unavailable;
        }

        log.warn("account-service unavailable ({}): {}", ex.getClass().getSimpleName(), ex.getMessage());
        throw new AccountServiceUnavailableException();
    }

    private void execute(UUID accountId, String uri, BigDecimal amount) {
        try {
            accountRestClient.post()
                    .uri(uri, accountId)
                    .body(Map.of("amount", amount))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new AccountNotFoundException(accountId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 422) {
                throw new InsufficientFundsException(accountId);
            }
            throw new AccountOperationRejectedException(
                    "Account operation rejected: " + e.getStatusCode());
        } catch (Exception e) {
            log.warn("account-service unavailable: {}", e.getMessage());
            throw new AccountServiceUnavailableException();
        }
    }
}
