package com.bank.transactionservice.infrastructure.config;


import com.bank.transactionservice.application.port.out.AccountPort;
import com.bank.transactionservice.application.port.out.TransactionRepositoryPort;
import com.bank.transactionservice.application.service.TransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplicationConfig {

    @Bean
    TransactionService transactionService(TransactionRepositoryPort transactionRepository,
                                          AccountPort accountPort) {
        return new TransactionService(transactionRepository, accountPort);
    }
}
