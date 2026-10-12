package com.bank.accountservice.infrastructure.out.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-service", url = "${customer-service.url}")
public interface CustomerFeignClient {

    @GetMapping("/api/v1/customers/{id}")
    CustomerClientResponse getCustomer(@PathVariable("id") UUID id);
}
