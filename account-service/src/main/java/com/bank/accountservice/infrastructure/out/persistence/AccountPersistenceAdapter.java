package com.bank.accountservice.infrastructure.out.persistence;


import com.bank.accountservice.application.ports.outbound.AccountRepositoryPort;
import com.bank.accountservice.domain.exception.AccountNotFoundException;
import com.bank.accountservice.domain.model.Account;
import com.bank.accountservice.domain.model.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;

    @Override
    public Account save(Account account) {
        var entity = jpaRepository.save(toEntity(account));
        return toDomain(entity);
    }

    @Override
    public Optional<Account> findByAccountId(AccountId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        var entity = jpaRepository.findByAccountNumber(accountNumber);
        return entity.map(this::toDomain);
    }

    @Override
    public List<Account> findByCustomerId(UUID customerId) {
        var entity = jpaRepository.findByCustomerId(customerId);
        return entity.stream().map(this::toDomain).toList();
    }

    private AccountJpaEntity toEntity(Account  account) {
        return AccountJpaEntity.builder()
                .id(account.getId().value())
                .customerId(account.getCustomerId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .build();
    }
    private Account toDomain(AccountJpaEntity entity) {
        return Account.reconstitute(
                AccountId.of(entity.getId()),
                entity.getCustomerId(),
                entity.getAccountNumber(),
                entity.getAccountType(),
                entity.getBalance(),
                entity.getStatus()
        );
    }
}
