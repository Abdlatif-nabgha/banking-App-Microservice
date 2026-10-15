package com.bank.accountservice.infrastructure.in.rest;

import com.bank.accountservice.application.ports.inbound.AccountUseCase;
import com.bank.accountservice.domain.model.Account;
import com.bank.accountservice.domain.model.AccountId;
import com.bank.accountservice.infrastructure.in.rest.dto.AccountResponse;
import com.bank.accountservice.infrastructure.in.rest.dto.AmountRequest;
import com.bank.accountservice.infrastructure.in.rest.dto.OpenAccountRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "Operations related to bank accounts")
public class AccountController {

    private final AccountUseCase accountUseCase;

    @PostMapping
    @Operation(summary = "Open an account", description = "Create a new bank account for an existing customer")
    public ResponseEntity<AccountResponse> open(
            @Valid @RequestBody OpenAccountRequest request) {
        Account account = accountUseCase.openAccount(request.toCommand());
        return ResponseEntity
                .created(URI.create("/api/accounts/" + account.getId().value()))
                .body(AccountResponse.from(account));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Retrieve account details by unique account UUID")
    public AccountResponse get(@PathVariable UUID id) {
        return AccountResponse.from(accountUseCase.getAccount(AccountId.of(id)));
    }

    @GetMapping
    @Operation(summary = "Get accounts by customer ID", description = "Retrieve all accounts belonging to a specific customer")
    public List<AccountResponse> getByCustomer(@RequestParam UUID customerId) {
        return accountUseCase.getAccountsByCustomerId(customerId).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @PostMapping("/{id}/deposit")
    @Operation(summary = "Deposit money", description = "Deposit an amount into an active account")
    public AccountResponse deposit(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        return AccountResponse.from(accountUseCase.deposit(AccountId.of(id), request.amount()));
    }

    @PostMapping("/{id}/withdraw")
    @Operation(summary = "Withdraw money", description = "Withdraw an amount from an active account with sufficient funds")
    public AccountResponse withdraw(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        return AccountResponse.from(accountUseCase.withdraw(AccountId.of(id), request.amount()));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Suspend account", description = "Temporarily suspend an account")
    public void suspend(@PathVariable UUID id) {
        accountUseCase.suspendAccount(AccountId.of(id));
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Activate account", description = "Activate a suspended account")
    public void activate(@PathVariable UUID id) {
        accountUseCase.activateAccount(AccountId.of(id));
    }

    @PostMapping("/{id}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Close account", description = "Close an account with a zero balance")
    public void close(@PathVariable UUID id) {
        accountUseCase.closeAccount(AccountId.of(id));
    }
}
