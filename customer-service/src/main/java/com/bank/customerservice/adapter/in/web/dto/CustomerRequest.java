package com.bank.customerservice.adapter.in.web.dto;

import com.bank.customerservice.application.port.in.CustomerUseCase;
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
