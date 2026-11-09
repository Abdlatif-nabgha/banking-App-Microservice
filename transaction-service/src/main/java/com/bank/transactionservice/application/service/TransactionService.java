package com.bank.transactionservice.application.service;


import com.bank.transactionservice.application.port.in.DepositUseCase;
import com.bank.transactionservice.application.port.in.GetTransactionUseCase;
import com.bank.transactionservice.application.port.in.GetTransactionsByAccountUseCase;
import com.bank.transactionservice.application.port.in.TransferUseCase;
import com.bank.transactionservice.application.port.in.WithdrawUseCase;
import com.bank.transactionservice.application.port.out.AccountPort;
import com.bank.transactionservice.application.port.out.TransactionRepositoryPort;
import com.bank.transactionservice.domain.exception.TransactionNotFoundException;
import com.bank.transactionservice.domain.model.Transaction;
import com.bank.transactionservice.domain.model.TransactionId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;



public class TransactionService implements DepositUseCase,
                                           WithdrawUseCase,
                                           TransferUseCase,
                                           GetTransactionUseCase,
                                           GetTransactionsByAccountUseCase {

    private final TransactionRepositoryPort transactionRepository;
    private final AccountPort accountPort;

    public TransactionService(TransactionRepositoryPort transactionRepository,
                              AccountPort accountPort) {
        this.transactionRepository = transactionRepository;
        this.accountPort = accountPort;
    }

    // ── Deposit ───────────────────────────────────────────────────────

    @Override
    public Transaction deposit(UUID accountId, BigDecimal amount) {
        Transaction tx = Transaction.deposit(accountId, amount);

        try {
            accountPort.deposit(accountId, amount);
            tx.complete();
        } catch (Exception e) {
            tx.fail(e.getMessage());
        }

        return transactionRepository.save(tx);
    }

    // ── Withdraw ──────────────────────────────────────────────────────

    @Override
    public Transaction withdraw(UUID accountId, BigDecimal amount) {
        Transaction tx = Transaction.withdrawal(accountId, amount);

        try {
            accountPort.withdraw(accountId, amount);
            tx.complete();
        } catch (Exception e) {
            tx.fail(e.getMessage());
        }

        return transactionRepository.save(tx);
    }

    // ── Transfer ──────────────────────────────────────────────────────

    @Override
    public Transaction transfer(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) {
        Transaction tx = Transaction.transfer(sourceAccountId, destinationAccountId, amount);
        boolean sourceDebited = false;

        try {
            // Step 1: Debit source account
            accountPort.withdraw(sourceAccountId, amount);
            sourceDebited = true;

            // Step 2: Credit destination account
            accountPort.deposit(destinationAccountId, amount);

            tx.complete();
        } catch (Exception e) {
            if (sourceDebited) {
                try {
                    // SAGA Compensation: Refund the source account
                    accountPort.deposit(sourceAccountId, amount);
                    tx.fail("Transfer failed at destination step. Source account was refunded: " + e.getMessage());
                } catch (Exception compensationException) {
                    // Critical case: alert for manual reconciliation
                    tx.fail("CRITICAL: Transfer failed and compensation failed. Manual intervention required.");
                }
            } else {
                tx.fail(e.getMessage());
            }
        }

        return transactionRepository.save(tx);
    }

    // ── Get Transaction ───────────────────────────────────────────────

    @Override
    public Transaction getTransaction(TransactionId id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    // ── Get Transactions By Account ───────────────────────────────────

    @Override
    public List<Transaction> getTransactionsByAccount(UUID accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
}
