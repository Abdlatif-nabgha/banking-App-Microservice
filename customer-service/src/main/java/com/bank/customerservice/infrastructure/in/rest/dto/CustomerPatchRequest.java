package com.bank.customerservice.infrastructure.in.rest.dto;


import com.bank.customerservice.application.ports.inbound.CustomerUseCase;

public record CustomerPatchRequest(
        String firstName,
        String lastName,
        String phoneNumber
) {
    public CustomerUseCase.CustomerPatchDetails toDetails() {
        return new CustomerUseCase.CustomerPatchDetails(
                firstName,
                lastName,
                phoneNumber);
    }
}
