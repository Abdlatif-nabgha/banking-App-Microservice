package com.bank.customerservice.infrastructure.in.rest;

import com.bank.customerservice.infrastructure.in.rest.dto.CustomerPatchRequest;
import com.bank.customerservice.infrastructure.in.rest.dto.CustomerRequest;
import com.bank.customerservice.infrastructure.in.rest.dto.CustomerResponse;
import com.bank.customerservice.infrastructure.in.rest.dto.CustomerUpdateRequest;
import com.bank.customerservice.application.ports.inbound.CustomerUseCase;
import com.bank.customerservice.domain.model.CustomerId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Operations related to customers")
public class CustomerController {

    private final CustomerUseCase customerUseCase;

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve a list of all registered customers")
    public List<CustomerResponse> getAllCustomers() {
        return customerUseCase.findAll().stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve customer details by their unique UUID")
    public CustomerResponse getCustomerById(@PathVariable UUID id) {
        return CustomerResponse.from(customerUseCase.findById(CustomerId.of(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create customer", description = "Register a new customer")
    public CustomerResponse createCustomer(@Valid @RequestBody CustomerRequest request) {
        return CustomerResponse.from(customerUseCase.create(request.toDetails()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update existing customer details. Email is not modifiable.")
    public CustomerResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        return CustomerResponse.from(customerUseCase.update(CustomerId.of(id), request.toDetails()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update customer", description = "Update mutable fields (name, phone) of a customer. Email cannot be modified.")
    public CustomerResponse patch(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerPatchRequest request
    ) {
        return CustomerResponse.from(customerUseCase.patch(CustomerId.of(id), request.toDetails()));
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete customer", description = "Delete a customer by ID")
    public void delete(@PathVariable UUID id) {
        customerUseCase.delete(CustomerId.of(id));
    }
}
