package com.bank.transactionservice.infrastructure.adapter.out.persistence;


import com.bank.transactionservice.domain.model.TransactionStatus;
import com.bank.transactionservice.domain.model.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder
public class TransactionJpaEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    private UUID sourceAccountId;

    private UUID destinationAccountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private String failureReason;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Long version;
}
