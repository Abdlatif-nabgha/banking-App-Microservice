package com.bank.accountservice.domain.model;


import com.bank.accountservice.domain.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Account {

    private final AccountId id;
    private final UUID customerId;
    private final String accountNumber;
    private final AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;

    private Account(AccountId id, UUID customerId, String accountNumber,
                    AccountType accountType, BigDecimal balance, AccountStatus status) {
        this.id = Objects.requireNonNull(id);
        this.customerId = Objects.requireNonNull(customerId);
        this.accountNumber = Objects.requireNonNull(accountNumber);
        this.accountType = Objects.requireNonNull(accountType);
        this.balance = Objects.requireNonNull(balance);
        this.status = Objects.requireNonNull(status);
    }

    public static Account open(UUID customerId, String accountNumber, AccountType accountType) {
        return new Account(
                AccountId.generate(),
                customerId,
                accountNumber,
                accountType,
                BigDecimal.ZERO,
                AccountStatus.ACTIVE
        );
    }

    public static Account reconstitute(AccountId id, UUID customerId, String accountNumber,
                                       AccountType accountType, BigDecimal balance,
                                       AccountStatus status) {
        return new Account(id, customerId, accountNumber, accountType, balance, status);
    }

    public void suspend() {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot suspend a closed account");
        }
        this.status = AccountStatus.SUSPENDED;
    }

    public void activate() {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot reactivate a closed account");
        }
        this.status = AccountStatus.ACTIVE;
    }

    public void close() {
        if (balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot close an account with a non-zero balance");
        }
        this.status = AccountStatus.CLOSED;
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot deposit into a non-active account");
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be greater than zero");
        }
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot withdraw from a non-active account");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(this.id, this.balance);
        }
        this.balance = this.balance.subtract(amount);
    }

    // Getters
    public AccountId getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getAccountNumber() { return accountNumber; }
    public AccountType getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
}
