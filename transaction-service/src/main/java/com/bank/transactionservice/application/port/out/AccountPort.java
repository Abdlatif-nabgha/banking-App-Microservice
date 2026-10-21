package com.bank.transactionservice.application.port.out;


import java.math.BigDecimal;
import java.util.UUID;

public interface AccountPort {
    void withdraw(UUID accountId, BigDecimal amount);
    void deposit(UUID accountId, BigDecimal amount);
}
