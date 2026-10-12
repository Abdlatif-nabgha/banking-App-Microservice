package com.bank.accountservice.application.services;

import com.bank.accountservice.application.ports.inbound.OpenAccountCommand;
import com.bank.accountservice.application.ports.outbound.AccountRepositoryPort;
import com.bank.accountservice.application.ports.outbound.CustomerVerificationPort;
import com.bank.accountservice.domain.exception.AccountNotFoundException;
import com.bank.accountservice.domain.exception.CustomerNotFoundException;
import com.bank.accountservice.domain.exception.InsufficientFundsException;
import com.bank.accountservice.domain.model.Account;
import com.bank.accountservice.domain.model.AccountId;
import com.bank.accountservice.domain.model.AccountStatus;
import com.bank.accountservice.domain.model.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @Mock
    private CustomerVerificationPort customerVerificationPort;

    @InjectMocks
    private AccountService accountService;

    private AccountId accountId1;
    private UUID customerId1;
    private Account account1;

    @BeforeEach
    void setUp() {
        accountId1 = AccountId.generate();
        customerId1 = UUID.randomUUID();

        account1 = Account.reconstitute(
                accountId1,
                customerId1,
                "ACC001",
                AccountType.SAVINGS,
                new BigDecimal("1000.00"),
                AccountStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("openAccount should create and save account when customer exists")
    void openAccount_Success() {
        when(customerVerificationPort.customerExists(customerId1)).thenReturn(true);
        when(accountRepositoryPort.findByAccountNumber(anyString())).thenReturn(Optional.empty());
        when(accountRepositoryPort.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OpenAccountCommand command = new OpenAccountCommand(customerId1, AccountType.SAVINGS);
        Account savedAccount = accountService.openAccount(command);

        assertNotNull(savedAccount);
        assertEquals(customerId1, savedAccount.getCustomerId());
        assertEquals(AccountType.SAVINGS, savedAccount.getAccountType());
        assertEquals(AccountStatus.ACTIVE, savedAccount.getStatus());
        assertEquals(BigDecimal.ZERO, savedAccount.getBalance());

        verify(customerVerificationPort, times(1)).customerExists(customerId1);
        verify(accountRepositoryPort, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("openAccount should throw CustomerNotFoundException when customer does not exist")
    void openAccount_CustomerNotFound() {
        when(customerVerificationPort.customerExists(customerId1)).thenReturn(false);

        OpenAccountCommand command = new OpenAccountCommand(customerId1, AccountType.SAVINGS);

        assertThrows(CustomerNotFoundException.class, () -> accountService.openAccount(command));

        verify(customerVerificationPort, times(1)).customerExists(customerId1);
        verify(accountRepositoryPort, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("getAccount should return account when found")
    void getAccount_Success() {
        when(accountRepositoryPort.findByAccountId(accountId1)).thenReturn(Optional.of(account1));

        Account found = accountService.getAccount(accountId1);

        assertNotNull(found);
        assertEquals(accountId1, found.getId());
        assertEquals("ACC001", found.getAccountNumber());
        verify(accountRepositoryPort, times(1)).findByAccountId(accountId1);
    }

    @Test
    @DisplayName("getAccount should throw AccountNotFoundException when account not found")
    void getAccount_NotFound() {
        when(accountRepositoryPort.findByAccountId(accountId1)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(accountId1));
        verify(accountRepositoryPort, times(1)).findByAccountId(accountId1);
    }

    @Test
    @DisplayName("getAccountsByCustomerId should return all accounts for a customer")
    void getAccountsByCustomerId() {
        when(accountRepositoryPort.findByCustomerId(customerId1)).thenReturn(List.of(account1));

        List<Account> accounts = accountService.getAccountsByCustomerId(customerId1);

        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertEquals(customerId1, accounts.get(0).getCustomerId());
        verify(accountRepositoryPort, times(1)).findByCustomerId(customerId1);
    }

    @Test
    @DisplayName("deposit should increase account balance and save")
    void depositFunds() {
        when(accountRepositoryPort.findByAccountId(accountId1)).thenReturn(Optional.of(account1));
        when(accountRepositoryPort.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account updated = accountService.deposit(accountId1, new BigDecimal("200.00"));

        assertEquals(new BigDecimal("1200.00"), updated.getBalance());
        verify(accountRepositoryPort, times(1)).findByAccountId(accountId1);
        verify(accountRepositoryPort, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("withdraw should decrease account balance and save")
    void withdrawFunds() {
        when(accountRepositoryPort.findByAccountId(accountId1)).thenReturn(Optional.of(account1));
        when(accountRepositoryPort.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account updated = accountService.withdraw(accountId1, new BigDecimal("100.00"));

        assertEquals(new BigDecimal("900.00"), updated.getBalance());
        verify(accountRepositoryPort, times(1)).findByAccountId(accountId1);
        verify(accountRepositoryPort, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("withdraw should throw InsufficientFundsException when balance is insufficient")
    void withdrawInsufficientFunds() {
        when(accountRepositoryPort.findByAccountId(accountId1)).thenReturn(Optional.of(account1));

        assertThrows(InsufficientFundsException.class, () ->
                accountService.withdraw(accountId1, new BigDecimal("1500.00"))
        );

        verify(accountRepositoryPort, times(1)).findByAccountId(accountId1);
        verify(accountRepositoryPort, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("suspendAccount should set status to SUSPENDED")
    void suspendAccount() {
        when(accountRepositoryPort.findByAccountId(accountId1)).thenReturn(Optional.of(account1));
        when(accountRepositoryPort.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.suspendAccount(accountId1);

        assertEquals(AccountStatus.SUSPENDED, account1.getStatus());
        verify(accountRepositoryPort, times(1)).save(account1);
    }

    @Test
    @DisplayName("activateAccount should reactivate a suspended account")
    void activateAccount() {
        account1.suspend();
        when(accountRepositoryPort.findByAccountId(accountId1)).thenReturn(Optional.of(account1));
        when(accountRepositoryPort.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.activateAccount(accountId1);

        assertEquals(AccountStatus.ACTIVE, account1.getStatus());
        verify(accountRepositoryPort, times(1)).save(account1);
    }
}
