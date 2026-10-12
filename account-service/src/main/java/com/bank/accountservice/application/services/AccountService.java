package com.bank.accountservice.application.services;


import com.bank.accountservice.application.ports.inbound.AccountUseCase;
import com.bank.accountservice.application.ports.inbound.OpenAccountCommand;
import com.bank.accountservice.application.ports.outbound.AccountRepositoryPort;
import com.bank.accountservice.application.ports.outbound.CustomerVerificationPort;
import com.bank.accountservice.domain.exception.AccountNotFoundException;
import com.bank.accountservice.domain.exception.CustomerNotFoundException;
import com.bank.accountservice.domain.model.Account;
import com.bank.accountservice.domain.model.AccountId;
import lombok.RequiredArgsConstructor;
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
        String number;
        do {
            number = "ACC" + ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        } while (accountRepositoryPort.findByAccountNumber(number).isPresent());
        return number;
    }

    @Override
    public Account getAccount(AccountId accountId) {
        return accountRepositoryPort.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    public List<Account> getAccountsByCustomerId(UUID customerId) {
        return accountRepositoryPort.findByCustomerId(customerId);
    }

    @Override
    @Transactional
    public Account deposit(AccountId id, BigDecimal amount) {
        Account account = getAccount(id);
        account.deposit(amount);
        return accountRepositoryPort.save(account);
    }

    @Override
    @Transactional
    public Account withdraw(AccountId id, BigDecimal amount) {
        Account account = getAccount(id);
        account.withdraw(amount);
        return accountRepositoryPort.save(account);
    }

    @Override
    @Transactional
    public void suspendAccount(AccountId id) {
        Account account = getAccount(id);
        account.suspend();
        accountRepositoryPort.save(account);
    }

    @Override
    @Transactional
    public void activateAccount(AccountId id) {
        Account account = getAccount(id);
        account.activate();
        accountRepositoryPort.save(account);
    }

    @Override
    @Transactional
    public void closeAccount(AccountId id) {
        Account account = getAccount(id);
        account.close();
        accountRepositoryPort.save(account);
    }
}
