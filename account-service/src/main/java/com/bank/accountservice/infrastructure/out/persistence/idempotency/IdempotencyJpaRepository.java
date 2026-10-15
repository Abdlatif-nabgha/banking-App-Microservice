package com.bank.accountservice.infrastructure.out.persistence.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyJpaRepository extends JpaRepository<IdempotencyRecordJpaEntity, String> {
}
