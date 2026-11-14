package com.bank.accountservice.application.ports.outbound;


import java.util.Optional;
import java.util.UUID;

public interface CustomerVerificationPort {
    Optional<CustomerInfo> findCustomer(UUID customerId);
}
