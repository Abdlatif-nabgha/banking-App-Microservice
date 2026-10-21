package com.bank.transactionservice.application.port.out;


import com.bank.transactionservice.domain.model.Transaction;
import com.bank.transactionservice.domain.model.TransactionId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(TransactionId id);
    List<Transaction> findByAccountId(UUID accountId);   // source OU destination
}
