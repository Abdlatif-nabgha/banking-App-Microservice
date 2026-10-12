package com.bank.accountservice.infrastructure.in.rest.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;

public record AmountRequest(
        @NotNull @Positive BigDecimal amount) {
}
