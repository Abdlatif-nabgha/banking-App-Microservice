package com.bank.transactionservice.infrastructure.adapter.in.rest;


import com.bank.transactionservice.application.port.in.DepositUseCase;
import com.bank.transactionservice.application.port.in.GetTransactionUseCase;
import com.bank.transactionservice.application.port.in.GetTransactionsByAccountUseCase;
import com.bank.transactionservice.application.port.in.TransferUseCase;
import com.bank.transactionservice.application.port.in.WithdrawUseCase;
import com.bank.transactionservice.domain.model.Transaction;
import com.bank.transactionservice.domain.model.TransactionId;
import com.bank.transactionservice.infrastructure.adapter.in.rest.dto.DepositRequest;
import com.bank.transactionservice.infrastructure.adapter.in.rest.dto.TransactionResponse;
import com.bank.transactionservice.infrastructure.adapter.in.rest.dto.TransferRequest;
import com.bank.transactionservice.infrastructure.adapter.in.rest.dto.WithdrawRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Adapter entrant REST.
 * <p>
 * Remarque : le contrôleur injecte chaque use case <strong>séparément</strong>
 * — il ne connaît pas {@code TransactionService}.
 * C'est le principe ISP appliqué à l'injection de dépendances.
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Management", description = "Operations related to banking transactions")
public class TransactionController {

    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final TransferUseCase transferUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final GetTransactionsByAccountUseCase getTransactionsByAccountUseCase;

    @PostMapping("/deposit")
    @Operation(summary = "Deposit money", description = "Create a deposit transaction on an account")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request) {
        Transaction tx = depositUseCase.deposit(request.accountId(), request.amount());
        return ResponseEntity
                .created(URI.create("/api/v1/transactions/" + tx.getId().value()))
                .body(TransactionResponse.from(tx));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw money", description = "Create a withdrawal transaction from an account")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawRequest request) {
        Transaction tx = withdrawUseCase.withdraw(request.accountId(), request.amount());
        return ResponseEntity
                .created(URI.create("/api/v1/transactions/" + tx.getId().value()))
                .body(TransactionResponse.from(tx));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer money", description = "Transfer an amount between two accounts")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        Transaction tx = transferUseCase.transfer(
                request.sourceAccountId(),
                request.destinationAccountId(),
                request.amount());
        return ResponseEntity
                .created(URI.create("/api/v1/transactions/" + tx.getId().value()))
                .body(TransactionResponse.from(tx));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction", description = "Retrieve a transaction by its unique ID")
    public TransactionResponse get(@PathVariable UUID id) {
        return TransactionResponse.from(
                getTransactionUseCase.getTransaction(TransactionId.of(id)));
    }

    @GetMapping
    @Operation(summary = "Get transactions by account", description = "List all transactions for a given account")
    public List<TransactionResponse> getByAccount(@RequestParam UUID accountId) {
        return getTransactionsByAccountUseCase.getTransactionsByAccount(accountId).stream()
                .map(TransactionResponse::from)
                .toList();
    }
}
