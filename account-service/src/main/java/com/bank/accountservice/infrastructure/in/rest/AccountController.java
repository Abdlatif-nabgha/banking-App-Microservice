package com.bank.accountservice.infrastructure.in.rest;

import com.bank.accountservice.application.ports.inbound.AccountUseCase;
import com.bank.accountservice.domain.model.Account;
import com.bank.accountservice.domain.model.AccountId;
import com.bank.accountservice.infrastructure.in.rest.dto.AccountResponse;
import com.bank.accountservice.infrastructure.in.rest.dto.AmountRequest;
import com.bank.accountservice.infrastructure.in.rest.dto.OpenAccountRequest;
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
public class AccountController {

    private final AccountUseCase accountUseCase;

    @PostMapping
    public ResponseEntity<AccountResponse> open(@Valid @RequestBody OpenAccountRequest request) {
        Account account = accountUseCase.openAccount(request.toCommand());
        return ResponseEntity
                .created(URI.create("/api/accounts/" + account.getId().value()))
                .body(AccountResponse.from(account));
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable UUID id) {
        return AccountResponse.from(accountUseCase.getAccount(AccountId.of(id)));
    }

    @GetMapping
    public List<AccountResponse> getByCustomer(@RequestParam UUID customerId) {
        return accountUseCase.getAccountsByCustomerId(customerId).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @PostMapping("/{id}/deposit")
    public AccountResponse deposit(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        return AccountResponse.from(accountUseCase.deposit(AccountId.of(id), request.amount()));
    }

    @PostMapping("/{id}/withdraw")
    public AccountResponse withdraw(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        return AccountResponse.from(accountUseCase.withdraw(AccountId.of(id), request.amount()));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable UUID id) {
        accountUseCase.suspendAccount(AccountId.of(id));
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable UUID id) {
        accountUseCase.activateAccount(AccountId.of(id));
    }

    @PostMapping("/{id}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void close(@PathVariable UUID id) {
        accountUseCase.closeAccount(AccountId.of(id));
    }
}
