package com.bank.accountservice.application.ports.outbound;


import com.bank.accountservice.domain.model.Account;
import com.bank.accountservice.domain.model.AccountId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {
    Account save(Account account);
    Optional<Account> findById(AccountId id);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByCustomerId(UUID customerId);
}
