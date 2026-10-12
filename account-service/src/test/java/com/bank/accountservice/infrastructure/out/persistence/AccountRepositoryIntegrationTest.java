package com.bank.accountservice.infrastructure.out.persistence;

import com.bank.accountservice.domain.model.AccountStatus;
import com.bank.accountservice.domain.model.AccountType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryIntegrationTest {

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @Test
    void whenSaveAccount_thenCanFindById() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        AccountJpaEntity account = AccountJpaEntity.builder()
                .id(accountId)
                .customerId(customerId)
                .accountNumber("ACC" + UUID.randomUUID().toString().substring(0, 10))
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("2500.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        AccountJpaEntity savedAccount = accountJpaRepository.save(account);

        assertNotNull(savedAccount.getId());
        Optional<AccountJpaEntity> foundAccount = accountJpaRepository.findById(savedAccount.getId());
        assertTrue(foundAccount.isPresent());
        assertEquals(account.getAccountNumber(), foundAccount.get().getAccountNumber());
        assertEquals(new BigDecimal("2500.00"), foundAccount.get().getBalance());
    }

    @Test
    void whenFindByAccountNumber_thenAccountIsFound() {
        String accountNumber = "ACC" + UUID.randomUUID().toString().substring(0, 10);
        UUID customerId = UUID.randomUUID();

        AccountJpaEntity account = AccountJpaEntity.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .accountNumber(accountNumber)
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("5000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        accountJpaRepository.save(account);

        Optional<AccountJpaEntity> foundAccount = accountJpaRepository.findByAccountNumber(accountNumber);
        assertTrue(foundAccount.isPresent());
        assertEquals(customerId, foundAccount.get().getCustomerId());
        assertEquals(AccountType.SAVINGS, foundAccount.get().getAccountType());
    }

    @Test
    void whenFindByCustomerId_thenAccountsAreFound() {
        UUID customerId = UUID.randomUUID();

        AccountJpaEntity account1 = AccountJpaEntity.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .accountNumber("ACC" + UUID.randomUUID().toString().substring(0, 10))
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("100.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        AccountJpaEntity account2 = AccountJpaEntity.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .accountNumber("ACC" + UUID.randomUUID().toString().substring(0, 10))
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("200.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        accountJpaRepository.save(account1);
        accountJpaRepository.save(account2);

        List<AccountJpaEntity> accounts = accountJpaRepository.findByCustomerId(customerId);
        assertFalse(accounts.isEmpty());
        assertEquals(2, accounts.size());
    }
}
