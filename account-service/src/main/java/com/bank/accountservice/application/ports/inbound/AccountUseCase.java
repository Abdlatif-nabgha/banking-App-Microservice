package com.bank.accountservice.application.ports.inbound;


import com.bank.accountservice.domain.model.Account;
import com.bank.accountservice.domain.model.AccountId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountUseCase {
    Account openAccount(OpenAccountCommand command);
    Account getAccount(AccountId accountId);
    List<Account> getAccountsByCustomerId(UUID customerId);
    Account deposit(AccountId id, BigDecimal amount);
    Account withdraw(AccountId id, BigDecimal amount);
    void suspendAccount(AccountId id);
    void activateAccount(AccountId id);
    void closeAccount(AccountId id);
}
