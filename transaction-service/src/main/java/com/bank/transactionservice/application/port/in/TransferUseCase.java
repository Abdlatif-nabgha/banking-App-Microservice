package com.bank.transactionservice.application.port.in;


import com.bank.transactionservice.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransferUseCase {
    Transaction transfer(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount);
}
