package com.bank.transactionservice.infrastructure.config;


import com.bank.transactionservice.application.port.in.DepositUseCase;
import com.bank.transactionservice.application.port.in.GetTransactionUseCase;
import com.bank.transactionservice.application.port.in.GetTransactionsByAccountUseCase;
import com.bank.transactionservice.application.port.in.TransferUseCase;
import com.bank.transactionservice.application.port.in.WithdrawUseCase;
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

    @Bean
    DepositUseCase depositUseCase(TransactionService service) {
        return service;
    }

    @Bean
    WithdrawUseCase withdrawUseCase(TransactionService service) {
        return service;
    }

    @Bean
    TransferUseCase transferUseCase(TransactionService service) {
        return service;
    }

    @Bean
    GetTransactionUseCase getTransactionUseCase(TransactionService service) {
        return service;
    }

    @Bean
    GetTransactionsByAccountUseCase getTransactionsByAccountUseCase(TransactionService service) {
        return service;
    }
}
