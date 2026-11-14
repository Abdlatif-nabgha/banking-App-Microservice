package com.bank.accountservice.application.ports.outbound;


import java.util.UUID;

public record CustomerInfo(
        UUID id,
        String firstName,
        String lastName,
        String email
) {}
