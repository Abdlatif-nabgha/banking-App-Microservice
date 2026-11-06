package com.bank.transactionservice.application.service;

import com.bank.transactionservice.application.port.out.AccountPort;
import com.bank.transactionservice.application.port.out.TransactionRepositoryPort;
import com.bank.transactionservice.domain.exception.TransactionNotFoundException;
import com.bank.transactionservice.domain.model.Transaction;
import com.bank.transactionservice.domain.model.TransactionId;
import com.bank.transactionservice.domain.model.TransactionStatus;
import com.bank.transactionservice.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private AccountPort accountPort;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        // Instanciation manuelle en pur Java
        transactionService = new TransactionService(transactionRepository, accountPort);
    }

    // ─────────────────────────────────────────────────────────────────
    // 1. DEPOSIT USE CASE
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("DepositUseCase Tests")
    class DepositTests {

        @Test
        @DisplayName("Should execute deposit successfully and mark transaction COMPLETED")
        void shouldDepositSuccessfully() {
            // Given
            UUID accountId = UUID.randomUUID();
            BigDecimal amount = new BigDecimal("100.00");
            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Transaction result = transactionService.deposit(accountId, amount);

            // Then (AssertJ)
            assertThat(result).isNotNull();
            assertThat(result.getType()).isEqualTo(TransactionType.DEPOSIT);
            assertThat(result.getDestinationAccountId()).isEqualTo(accountId);
            assertThat(result.getAmount()).isEqualByComparingTo(amount);
            assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);

            // Verify interactions on Outbound Ports
            verify(accountPort).deposit(accountId, amount);
            verify(transactionRepository).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Should mark transaction FAILED when accountPort throws exception")
        void shouldFailDepositWhenAccountServiceFails() {
            // Given
            UUID accountId = UUID.randomUUID();
            BigDecimal amount = new BigDecimal("100.00");
            doThrow(new RuntimeException("Account service unavailable"))
                    .when(accountPort).deposit(accountId, amount);

            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Transaction result = transactionService.deposit(accountId, amount);

            // Then
            assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
            assertThat(result.getFailureReason()).contains("Account service unavailable");
            verify(transactionRepository).save(result);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // 2. WITHDRAW USE CASE
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("WithdrawUseCase Tests")
    class WithdrawTests {

        @Test
        @DisplayName("Should execute withdrawal successfully and mark transaction COMPLETED")
        void shouldWithdrawSuccessfully() {
            // Given
            UUID accountId = UUID.randomUUID();
            BigDecimal amount = new BigDecimal("50.00");
            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Transaction result = transactionService.withdraw(accountId, amount);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getType()).isEqualTo(TransactionType.WITHDRAWAL);
            assertThat(result.getSourceAccountId()).isEqualTo(accountId);
            assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);

            verify(accountPort).withdraw(accountId, amount);
        }

        @Test
        @DisplayName("Should mark withdrawal FAILED if insufficient balance")
        void shouldFailWithdrawalOnInsufficientBalance() {
            // Given
            UUID accountId = UUID.randomUUID();
            BigDecimal amount = new BigDecimal("5000.00");
            doThrow(new RuntimeException("Insufficient funds"))
                    .when(accountPort).withdraw(accountId, amount);

            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Transaction result = transactionService.withdraw(accountId, amount);

            // Then
            assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
            assertThat(result.getFailureReason()).isEqualTo("Insufficient funds");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // 3. TRANSFER USE CASE
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("TransferUseCase Tests")
    class TransferTests {

        @Test
        @DisplayName("Should execute transfer between two accounts successfully")
        void shouldTransferSuccessfully() {
            // Given
            UUID sourceId = UUID.randomUUID();
            UUID destId = UUID.randomUUID();
            BigDecimal amount = new BigDecimal("250.00");

            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Transaction result = transactionService.transfer(sourceId, destId, amount);

            // Then
            assertThat(result.getType()).isEqualTo(TransactionType.TRANSFER);
            assertThat(result.getSourceAccountId()).isEqualTo(sourceId);
            assertThat(result.getDestinationAccountId()).isEqualTo(destId);
            assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);

            verify(accountPort).withdraw(sourceId, amount);
            verify(accountPort).deposit(destId, amount);
        }

        @Test
        @DisplayName("Should fail transfer if withdraw fails")
        void shouldFailTransferWhenSourceFails() {
            // Given
            UUID sourceId = UUID.randomUUID();
            UUID destId = UUID.randomUUID();
            BigDecimal amount = new BigDecimal("250.00");

            doThrow(new RuntimeException("Source account blocked"))
                    .when(accountPort).withdraw(sourceId, amount);

            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Transaction result = transactionService.transfer(sourceId, destId, amount);

            // Then
            assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
            assertThat(result.getFailureReason()).isEqualTo("Source account blocked");
            // Destination deposit should never be called!
            verify(accountPort, never()).deposit(eq(destId), any());
        }

        @Test
        @DisplayName("SAGA: Should compensate source account when destination deposit fails")
        void shouldCompensateSourceWhenDestinationFails() {
            // Given
            UUID sourceId = UUID.randomUUID();
            UUID destId = UUID.randomUUID();
            BigDecimal amount = new BigDecimal("250.00");

            doNothing().when(accountPort).withdraw(sourceId, amount);
            doThrow(new RuntimeException("Destination account not found"))
                    .when(accountPort).deposit(destId, amount);

            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Transaction result = transactionService.transfer(sourceId, destId, amount);

            // Then
            assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
            assertThat(result.getFailureReason()).contains("Source account was refunded");
            assertThat(result.getFailureReason()).contains("Destination account not found");

            // Verify SAGA compensation:
            verify(accountPort).withdraw(sourceId, amount);
            verify(accountPort).deposit(destId, amount);
            verify(accountPort).deposit(sourceId, amount); // Refund!
        }

        @Test
        @DisplayName("SAGA: Should record critical failure when both destination and compensation fail")
        void shouldHandleCompensationFailure() {
            // Given
            UUID sourceId = UUID.randomUUID();
            UUID destId = UUID.randomUUID();
            BigDecimal amount = new BigDecimal("250.00");

            doNothing().when(accountPort).withdraw(sourceId, amount);
            doThrow(new RuntimeException("Destination unavailable"))
                    .when(accountPort).deposit(destId, amount);
            doThrow(new RuntimeException("Account service totally down"))
                    .when(accountPort).deposit(sourceId, amount);

            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Transaction result = transactionService.transfer(sourceId, destId, amount);

            // Then
            assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
            assertThat(result.getFailureReason()).contains("CRITICAL: Transfer failed and compensation failed");

            verify(accountPort).withdraw(sourceId, amount);
            verify(accountPort).deposit(destId, amount);
            verify(accountPort).deposit(sourceId, amount);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // 4. GET TRANSACTION USE CASES
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Query UseCases Tests")
    class QueryTests {

        @Test
        @DisplayName("Should return transaction when found by ID")
        void shouldFindTransactionById() {
            // Given
            TransactionId txId = TransactionId.generate();
            Transaction existingTx = Transaction.deposit(UUID.randomUUID(), new BigDecimal("100"));
            when(transactionRepository.findById(txId)).thenReturn(Optional.of(existingTx));

            // When
            Transaction result = transactionService.getTransaction(txId);

            // Then
            assertThat(result).isEqualTo(existingTx);
            verify(transactionRepository).findById(txId);
        }

        @Test
        @DisplayName("Should throw TransactionNotFoundException when ID does not exist")
        void shouldThrowWhenTransactionNotFound() {
            // Given
            TransactionId txId = TransactionId.generate();
            when(transactionRepository.findById(txId)).thenReturn(Optional.empty());

            // When & Then (AssertJ exception testing)
            assertThatThrownBy(() -> transactionService.getTransaction(txId))
                    .isInstanceOf(TransactionNotFoundException.class);
        }

        @Test
        @DisplayName("Should return transactions list for an account")
        void shouldFindTransactionsByAccount() {
            // Given
            UUID accountId = UUID.randomUUID();
            List<Transaction> list = List.of(
                    Transaction.deposit(accountId, new BigDecimal("100")),
                    Transaction.withdrawal(accountId, new BigDecimal("50"))
            );
            when(transactionRepository.findByAccountId(accountId)).thenReturn(list);

            // When
            List<Transaction> result = transactionService.getTransactionsByAccount(accountId);

            // Then
            assertThat(result).hasSize(2).isEqualTo(list);
            verify(transactionRepository).findByAccountId(accountId);
        }
    }
}
