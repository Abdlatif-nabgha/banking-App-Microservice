package com.bank.customerservice.infrastructure.in.rest;

import com.bank.customerservice.infrastructure.in.rest.dto.CustomerRequest;
import com.bank.customerservice.infrastructure.in.rest.dto.CustomerResponse;
import com.bank.customerservice.application.ports.inbound.CustomerUseCase;
import com.bank.customerservice.domain.model.CustomerId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Management", description = "Operations related to customers")
public class CustomerController {

    private final CustomerUseCase useCase;

    public CustomerController(CustomerUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve a list of all registered customers")
    public List<CustomerResponse> getAllCustomers() {
        return useCase.findAll().stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve customer details by their unique UUID")
    public CustomerResponse getCustomerById(@PathVariable UUID id) {
        return CustomerResponse.from(useCase.findById(CustomerId.of(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create customer", description = "Register a new customer")
    public CustomerResponse createCustomer(@Valid @RequestBody CustomerRequest request) {
        return CustomerResponse.from(useCase.create(request.toDetails()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update existing customer details")
    public CustomerResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequest request
    ) {
        return CustomerResponse.from(useCase.update(CustomerId.of(id), request.toDetails()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete customer", description = "Delete a customer by ID")
    public void delete(@PathVariable UUID id) {
        useCase.delete(CustomerId.of(id));
    }
}
