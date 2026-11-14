package com.bank.accountservice.application.services;


import com.bank.accountservice.application.ports.inbound.AccountUseCase;
import com.bank.accountservice.application.ports.inbound.OpenAccountCommand;
import com.bank.accountservice.application.ports.outbound.AccountEventPublisherPort;
import com.bank.accountservice.application.ports.outbound.AccountRepositoryPort;
import com.bank.accountservice.application.ports.outbound.CustomerVerificationPort;
import com.bank.accountservice.domain.event.AccountOpenedEvent;
import com.bank.accountservice.domain.exception.AccountNotFoundException;
import com.bank.accountservice.domain.exception.CustomerNotFoundException;
import com.bank.accountservice.domain.model.Account;
import com.bank.accountservice.domain.model.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService implements AccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final CustomerVerificationPort customerVerificationPort;
    private final AccountEventPublisherPort eventPublisherPort;

    @Override
    @Cacheable(value = "allAccounts", key = "#page + '-' + #size")
    public List<Account> getAllAccounts(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page must be >= 0 and size must be > 0");
        }
        List<Account> accounts = accountRepositoryPort.findAll(page, size);
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No accounts found");
        }
        return accounts;
    }

    @Transactional
    @Override
    @CachePut(value = "accounts", key = "#result.id.value()")
    @Caching(evict = {
            @CacheEvict(value = "customerAccounts", key = "#command.customerId()"),
            @CacheEvict(value = "allAccounts", allEntries = true)
    })
    public Account openAccount(OpenAccountCommand command) {
        var customer = customerVerificationPort.findCustomer(command.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(command.customerId()));

        var account = Account.open(customer.id(), generateAccountNumber(), command.accountType());

        var savedAccount = accountRepositoryPort.save(account);

        eventPublisherPort.publishAccountOpened(
                new AccountOpenedEvent(
                        UUID.randomUUID(),
                        savedAccount.getId().value(),
                        customer.id(),
                        customer.firstName(),
                        customer.lastName(),
                        customer.email(),
                        savedAccount.getAccountNumber(),
                        savedAccount.getAccountType(),
                        Instant.now()
                )
        );
        return savedAccount;
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = "ACC" + ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        } while (accountRepositoryPort.findByAccountNumber(number).isPresent());
        return number;
    }

    @Override
    @Cacheable(value = "accounts", key = "#accountId.value()")
    public Account getAccount(AccountId accountId) {
        return findAccountOrThrow(accountId);
    }

    private Account findAccountOrThrow(AccountId accountId) {
        return accountRepositoryPort.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Cacheable(value = "customerAccounts", key = "#customerId")
    public List<Account> getAccountsByCustomerId(UUID customerId) {
        return accountRepositoryPort.findByCustomerId(customerId);
    }

    @Override
    @Transactional
    @CachePut(value = "accounts", key = "#id.value()")
    @Caching(evict = {
            @CacheEvict(value = "customerAccounts", allEntries = true),
            @CacheEvict(value = "allAccounts", allEntries = true)
    })
    public Account deposit(AccountId id, BigDecimal amount) {
        Account account = findAccountOrThrow(id);
        account.deposit(amount);
        return accountRepositoryPort.save(account);
    }

    @Override
    @Transactional
    @CachePut(value = "accounts", key = "#id.value()")
    @Caching(evict = {
            @CacheEvict(value = "customerAccounts", allEntries = true),
            @CacheEvict(value = "allAccounts", allEntries = true)
    })
    public Account withdraw(AccountId id, BigDecimal amount) {
        Account account = findAccountOrThrow(id);
        account.withdraw(amount);
        return accountRepositoryPort.save(account);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "accounts", key = "#id.value()"),
            @CacheEvict(value = "customerAccounts", allEntries = true),
            @CacheEvict(value = "allAccounts", allEntries = true)
    })
    public void suspendAccount(AccountId id) {
        Account account = findAccountOrThrow(id);
        account.suspend();
        accountRepositoryPort.save(account);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "accounts", key = "#id.value()"),
            @CacheEvict(value = "customerAccounts", allEntries = true),
            @CacheEvict(value = "allAccounts", allEntries = true)
    })
    public void activateAccount(AccountId id) {
        Account account = findAccountOrThrow(id);
        account.activate();
        accountRepositoryPort.save(account);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "accounts", key = "#id.value()"),
            @CacheEvict(value = "customerAccounts", allEntries = true),
            @CacheEvict(value = "allAccounts", allEntries = true)
    })
    public void closeAccount(AccountId id) {
        Account account = findAccountOrThrow(id);
        account.close();
        accountRepositoryPort.save(account);
    }
}
