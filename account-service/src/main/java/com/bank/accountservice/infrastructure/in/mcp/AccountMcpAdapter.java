package com.bank.accountservice.infrastructure.in.mcp;

import com.bank.accountservice.application.ports.inbound.AccountUseCase;
import com.bank.accountservice.application.ports.inbound.OpenAccountCommand;
import com.bank.accountservice.domain.model.AccountId;
import com.bank.accountservice.domain.model.AccountType;
import com.bank.accountservice.infrastructure.in.rest.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountMcpAdapter {

    private final AccountUseCase accountUseCase;

    @McpTool(description = "Get a paginated list of all bank accounts")
    public List<AccountResponse> getAllAccounts(
            @McpToolParam(description = "Page number starting from 0 (default 0)") Integer page,
            @McpToolParam(description = "Number of accounts per page (default 10)") Integer size
    ) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;
        return accountUseCase.getAllAccounts(pageNumber, pageSize).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @McpTool(description = "Get bank account details and balance by account UUID")
    public AccountResponse getAccountById(
            @McpToolParam(description = "The unique account UUID") UUID accountId
    ) {
        return AccountResponse.from(accountUseCase.getAccount(AccountId.of(accountId)));
    }

    @McpTool(description = "Get all bank accounts belonging to a specific customer UUID")
    public List<AccountResponse> getAccountsByCustomerId(
            @McpToolParam(description = "The customer UUID") UUID customerId
    ) {
        return accountUseCase.getAccountsByCustomerId(customerId).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @McpTool(description = "Open a new bank account (SAVINGS, CHECKING, or BUSINESS) for an existing customer")
    public AccountResponse openAccount(
            @McpToolParam(description = "The existing customer UUID") UUID customerId,
            @McpToolParam(description = "The account type: SAVINGS, CHECKING, or BUSINESS") AccountType accountType
    ) {
        return AccountResponse.from(accountUseCase.openAccount(new OpenAccountCommand(customerId, accountType)));
    }

    @McpTool(description = "Deposit money into an active bank account")
    public AccountResponse depositMoney(
            @McpToolParam(description = "The unique account UUID") UUID accountId,
            @McpToolParam(description = "The positive amount to deposit") BigDecimal amount
    ) {
        return AccountResponse.from(accountUseCase.deposit(AccountId.of(accountId), amount));
    }

    @McpTool(description = "Withdraw money from an active bank account")
    public AccountResponse withdrawMoney(
            @McpToolParam(description = "The unique account UUID") UUID accountId,
            @McpToolParam(description = "The positive amount to withdraw") BigDecimal amount
    ) {
        return AccountResponse.from(accountUseCase.withdraw(AccountId.of(accountId), amount));
    }
}
