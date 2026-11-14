package com.bank.accountservice.infrastructure.out.client;

import java.util.UUID;

public record CustomerClientResponse(
        UUID id,
        String firstName,
        String lastName,
        String email
) {}
