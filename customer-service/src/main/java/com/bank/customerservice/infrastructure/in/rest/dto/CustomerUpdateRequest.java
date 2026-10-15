package com.bank.customerservice.infrastructure.in.rest.dto;


import com.bank.customerservice.application.ports.inbound.CustomerUseCase;
import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        String phoneNumber
) {
    public CustomerUseCase.CustomerPutDetails toDetails() {
        return new CustomerUseCase.CustomerPutDetails(firstName, lastName, phoneNumber);
    }
}
