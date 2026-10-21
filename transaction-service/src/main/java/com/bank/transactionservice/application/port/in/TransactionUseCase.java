package com.bank.transactionservice.application.port.in;


import com.bank.transactionservice.domain.model.Transaction;
import com.bank.transactionservice.domain.model.TransactionId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TransactionUseCase {

    Transaction deposit(UUID accountId, BigDecimal amount);
    Transaction withdraw(UUID accountId, BigDecimal amount);
    Transaction transfer(UUID sourceId, UUID destinationId, BigDecimal amount);

    Transaction getTransaction(TransactionId id);
    List<Transaction> getTransactionsByAccount(UUID accountId);
}
