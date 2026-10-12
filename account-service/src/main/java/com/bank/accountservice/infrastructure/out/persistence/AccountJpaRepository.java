package com.bank.accountservice.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, UUID> {
    Optional<AccountJpaEntity> findByAccountNumber(String accountNumber);
    List<AccountJpaEntity> findByCustomerId(UUID customerId);
}