package com.bank.accountservice.infrastructure.out.client;


import com.bank.accountservice.application.ports.outbound.CustomerVerificationPort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerRestAdapter implements CustomerVerificationPort {

    private final CustomerFeignClient customerClient;

    @Override
    public boolean customerExists(UUID customerId) {
        try {
            customerClient.getCustomer(customerId);
            return true;
        } catch (FeignException.NotFound e) {
            return false;
        }
    }
}
