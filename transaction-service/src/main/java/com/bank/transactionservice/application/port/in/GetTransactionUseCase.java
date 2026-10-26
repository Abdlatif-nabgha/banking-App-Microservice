package com.bank.transactionservice.application.port.in;


import com.bank.transactionservice.domain.model.Transaction;
import com.bank.transactionservice.domain.model.TransactionId;

public interface GetTransactionUseCase {
    Transaction getTransaction(TransactionId id);
}
