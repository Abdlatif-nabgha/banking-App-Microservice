package com.bank.accountservice.application.ports.outbound;


import java.util.UUID;

public interface CustomerVerificationPort {
    boolean customerExists(UUID customerId);
}
