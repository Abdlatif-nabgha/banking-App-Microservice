package com.bank.transactionservice.domain.exception;

import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(UUID id) { super("Insufficient funds on account " + id); }
}

