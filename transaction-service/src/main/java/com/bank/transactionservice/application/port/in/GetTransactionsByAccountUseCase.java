package com.bank.transactionservice.application.port.in;


import com.bank.transactionservice.domain.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface GetTransactionsByAccountUseCase {
    List<Transaction> getTransactionsByAccount(UUID accountId);
}
