package com.bank.transactionservice.infrastructure.config;


import com.bank.transactionservice.application.port.out.AccountPort;
import com.bank.transactionservice.application.port.out.TransactionRepositoryPort;
import com.bank.transactionservice.application.service.TransactionService;
import com.bank.transactionservice.domain.model.Transaction;
import com.bank.transactionservice.domain.model.TransactionId;
import org.springframework.cache.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Configuration
@EnableCaching
public class ApplicationConfig {

    @Bean
    TransactionService transactionService(TransactionRepositoryPort transactionRepository,
                                          AccountPort accountPort) {
        return new CachingTransactionService(transactionRepository, accountPort);
    }

    static class CachingTransactionService extends TransactionService {

        public CachingTransactionService(TransactionRepositoryPort transactionRepository, AccountPort accountPort) {
            super(transactionRepository, accountPort);
        }

        @Override
        @Cacheable(value = "transactions", key = "#id.value()")
        public Transaction getTransaction(TransactionId id) {
            return super.getTransaction(id);
        }

        @Override
        @Cacheable(value = "accountTransactions", key = "#accountId")
        public List<Transaction> getTransactionsByAccount(UUID accountId) {
            return super.getTransactionsByAccount(accountId);
        }

        @Override
        @CachePut(value = "transactions", key = "#result.id.value()")
        @CacheEvict(value = "accountTransactions", key = "#accountId")
        public Transaction deposit(UUID accountId, BigDecimal amount) {
            return super.deposit(accountId, amount);
        }

        @Override
        @CachePut(value = "transactions", key = "#result.id.value()")
        @CacheEvict(value = "accountTransactions", key = "#accountId")
        public Transaction withdraw(UUID accountId, BigDecimal amount) {
            return super.withdraw(accountId, amount);
        }

        @Override
        @CachePut(value = "transactions", key = "#result.id.value()")
        @Caching(evict = {
                @CacheEvict(value = "accountTransactions", key = "#sourceAccountId"),
                @CacheEvict(value = "accountTransactions", key = "#destinationAccountId")
        })
        public Transaction transfer(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) {
            return super.transfer(sourceAccountId, destinationAccountId, amount);
        }
    }
}
