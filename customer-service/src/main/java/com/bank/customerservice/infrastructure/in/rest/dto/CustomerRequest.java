package com.bank.customerservice.infrastructure.in.rest.dto;

import com.bank.customerservice.application.ports.inbound.CustomerUseCase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        String phoneNumber
) {
    public CustomerUseCase.CustomerDetails toDetails() {
        return new CustomerUseCase.CustomerDetails(firstName, lastName, email, phoneNumber);
    }
}
