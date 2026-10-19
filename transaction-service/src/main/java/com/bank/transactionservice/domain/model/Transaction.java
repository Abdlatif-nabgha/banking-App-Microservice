package com.bank.transactionservice.domain.model;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

    private final TransactionId id;
    private final TransactionType type;
    private final UUID sourceAccountId;        // null pour un dépôt
    private final UUID destinationAccountId;   // null pour un retrait
    private final BigDecimal amount;
    private final Instant createdAt;
    private TransactionStatus status;
    private String failureReason;

    private Transaction(TransactionId id, TransactionType type, UUID sourceAccountId,
                        UUID destinationAccountId, BigDecimal amount, TransactionStatus status,
                        String failureReason, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = Objects.requireNonNull(amount);
        this.status = Objects.requireNonNull(status);
        this.failureReason = failureReason;
        this.createdAt = Objects.requireNonNull(createdAt);
    }


    public static Transaction deposit(UUID accountId, BigDecimal amount) {
        Objects.requireNonNull(accountId, "accountId cannot be null");
        requirePositive(amount);
        return pending(TransactionType.DEPOSIT, null, accountId, amount);
    }

    public static Transaction withdrawal(UUID accountId, BigDecimal amount) {
        Objects.requireNonNull(accountId, "accountId cannot be null");
        requirePositive(amount);
        return pending(TransactionType.WITHDRAWAL, accountId, null, amount);
    }

    public static Transaction transfer(UUID sourceId, UUID destinationId, BigDecimal amount) {
        Objects.requireNonNull(sourceId, "sourceAccountId cannot be null");
        Objects.requireNonNull(destinationId, "destinationAccountId cannot be null");
        if (sourceId.equals(destinationId)) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }
        requirePositive(amount);
        return pending(TransactionType.TRANSFER, sourceId, destinationId, amount);
    }

    // Réservée à l'adapter de persistance
    public static Transaction reconstitute(TransactionId id, TransactionType type, UUID sourceAccountId,
                                           UUID destinationAccountId, BigDecimal amount,
                                           TransactionStatus status, String failureReason,
                                           Instant createdAt) {
        return new Transaction(id, type, sourceAccountId, destinationAccountId,
                amount, status, failureReason, createdAt);
    }

    // ---------- Transitions d'état : uniquement depuis PENDING ----------

    public void complete() {
        requirePending();
        this.status = TransactionStatus.COMPLETED;
    }

    public void fail(String reason) {
        requirePending();
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Failure reason is required");
        }
        this.status = TransactionStatus.FAILED;
        this.failureReason = reason;
    }

    // ---------- Internes ----------

    private static Transaction pending(TransactionType type, UUID source, UUID destination,
                                       BigDecimal amount) {
        return new Transaction(TransactionId.generate(), type, source, destination, amount,
                TransactionStatus.PENDING, null, Instant.now());
    }

    private static void requirePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private void requirePending() {
        if (status != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction is already " + status);
        }
    }

    // ---------- Getters ----------

    public TransactionId getId() { return id; }
    public TransactionType getType() { return type; }
    public UUID getSourceAccountId() { return sourceAccountId; }
    public UUID getDestinationAccountId() { return destinationAccountId; }
    public BigDecimal getAmount() { return amount; }
    public TransactionStatus getStatus() { return status; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }
}