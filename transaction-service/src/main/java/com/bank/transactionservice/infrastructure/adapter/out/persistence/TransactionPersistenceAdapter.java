package com.bank.transactionservice.infrastructure.adapter.out.persistence;


import com.bank.transactionservice.application.port.out.TransactionRepositoryPort;
import com.bank.transactionservice.domain.model.Transaction;
import com.bank.transactionservice.domain.model.TransactionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository jpaRepository;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity entity = toEntity(transaction);
        TransactionJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Transaction> findByAccountId(UUID accountId) {
        return jpaRepository.findByAccountId(accountId).stream()
                .map(this::toDomain)
                .toList();
    }

    // ── Mapping ──────────────────────────────────────────────────────

    private TransactionJpaEntity toEntity(Transaction tx) {
        return TransactionJpaEntity.builder()
                .id(tx.getId().value())
                .type(tx.getType())
                .sourceAccountId(tx.getSourceAccountId())
                .destinationAccountId(tx.getDestinationAccountId())
                .amount(tx.getAmount())
                .status(tx.getStatus())
                .failureReason(tx.getFailureReason())
                .createdAt(tx.getCreatedAt())
                .build();
    }

    private Transaction toDomain(TransactionJpaEntity entity) {
        return Transaction.reconstitute(
                TransactionId.of(entity.getId()),
                entity.getType(),
                entity.getSourceAccountId(),
                entity.getDestinationAccountId(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getFailureReason(),
                entity.getCreatedAt()
        );
    }
}
