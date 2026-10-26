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


/**
 * Application service — cœur de l'hexagone.
 * <p>
 * Cette classe est <strong>framework-agnostic</strong> : aucune annotation Spring
 * ({@code @Service}, {@code @Transactional}, {@code @Slf4j}, etc.).
 * L'enregistrement comme bean Spring est délégué à
 * {@code infrastructure.config.ApplicationConfig}.
 * <p>
 * Elle implémente chaque port entrant (use case) séparément,
 * conformément au principe ISP (Interface Segregation Principle).
 */
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

        try {
            accountPort.withdraw(sourceAccountId, amount);
            accountPort.deposit(destinationAccountId, amount);
            tx.complete();
        } catch (Exception e) {
            tx.fail(e.getMessage());
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
