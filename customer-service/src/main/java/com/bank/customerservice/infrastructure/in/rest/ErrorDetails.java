package com.bank.customerservice.infrastructure.in.rest;

import java.time.LocalDateTime;

public record ErrorDetails(
        LocalDateTime timestamp,
        String message,
        String details,
        String path
) {}
