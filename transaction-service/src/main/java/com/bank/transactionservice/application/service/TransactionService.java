package com.bank.transactionservice.application.service;


import com.bank.transactionservice.application.port.in.TransactionUseCase;
import com.bank.transactionservice.application.port.out.AccountPort;
import com.bank.transactionservice.application.port.out.TransactionRepositoryPort;
import com.bank.transactionservice.domain.model.Transaction;
import com.bank.transactionservice.domain.model.TransactionId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService implements TransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final AccountPort accountPort;

    @Override
    public Transaction deposit(UUID accountId, BigDecimal amount) {
        return null;
    }

    @Override
    public Transaction withdraw(UUID accountId, BigDecimal amount) {
        return null;
    }

    @Override
    public Transaction transfer(UUID sourceId, UUID destinationId, BigDecimal amount) {
        return null;
    }

    @Override
    public Transaction getTransaction(TransactionId id) {
        return null;
    }

    @Override
    public List<Transaction> getTransactionsByAccount(UUID accountId) {
        return List.of();
    }
}
