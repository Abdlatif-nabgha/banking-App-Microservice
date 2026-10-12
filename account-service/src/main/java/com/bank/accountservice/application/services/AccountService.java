package com.bank.accountservice.application.services;


import com.bank.accountservice.application.ports.inbound.AccountUseCase;
import com.bank.accountservice.application.ports.inbound.OpenAccountCommand;
import com.bank.accountservice.application.ports.outbound.AccountRepositoryPort;
import com.bank.accountservice.application.ports.outbound.CustomerVerificationPort;
import com.bank.accountservice.domain.exception.CustomerNotFoundException;
import com.bank.accountservice.domain.model.Account;
import com.bank.accountservice.domain.model.AccountId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService implements AccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final CustomerVerificationPort customerVerificationPort;

    @Transactional
    @Override
    public Account openAccount(OpenAccountCommand command) {
        if (!customerVerificationPort.customerExists(command.customerId())) {
            throw new CustomerNotFoundException(command.customerId());
        }

        Account account = Account.open(command.customerId(), generateAccountNumber(), command.accountType());

        return accountRepositoryPort.save(account);
    }

    private String generateAccountNumber() {
        return "ACC" + ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
    }

    @Override
    public Account getAccount(AccountId accountId) {
        return null;
    }

    @Override
    public List<Account> getAccountsByCustomerId(UUID customerId) {
        return List.of();
    }

    @Override
    public Account deposit(AccountId id, BigDecimal amount) {
        return null;
    }

    @Override
    public Account withdraw(AccountId id, BigDecimal amount) {
        return null;
    }

    @Override
    public void suspendAccount(AccountId id) {

    }

    @Override
    public void activateAccount(AccountId id) {

    }

    @Override
    public void closeAccount(AccountId id) {

    }
}
