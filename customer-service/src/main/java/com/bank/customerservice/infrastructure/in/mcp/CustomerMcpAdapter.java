package com.bank.customerservice.infrastructure.in.mcp;

import com.bank.customerservice.application.ports.inbound.CustomerUseCase;
import com.bank.customerservice.domain.model.CustomerId;
import com.bank.customerservice.infrastructure.in.rest.dto.CustomerRequest;
import com.bank.customerservice.infrastructure.in.rest.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerMcpAdapter {

    private final CustomerUseCase customerUseCase;

    @McpTool(description = "Get all registered customers")
    public List<CustomerResponse> getAllCustomers() {
        return customerUseCase.findAll().stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @McpTool(description = "Find a customer by their unique UUID")
    public CustomerResponse getCustomerById(
            @McpToolParam(description = "The customer UUID") UUID id
    ) {
        return CustomerResponse.from(customerUseCase.findById(CustomerId.of(id)));
    }

    @McpTool(description = "Create and register a new customer")
    public CustomerResponse createCustomer(
            @McpToolParam(description = "Customer details (firstName, lastName, email, phoneNumber)") CustomerRequest request
    ) {
        return CustomerResponse.from(customerUseCase.create(request.toDetails()));
    }

    @McpTool(description = "Delete a customer by their unique UUID")
    public void deleteCustomer(
            @McpToolParam(description = "The customer UUID") UUID id
    ) {
        customerUseCase.delete(CustomerId.of(id));
    }
}

