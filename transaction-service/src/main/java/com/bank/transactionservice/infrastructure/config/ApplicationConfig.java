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


/**
 * Configuration Spring pour les beans applicatifs.
 * <p>
 * C'est ici — et uniquement ici — que Spring « découvre » le service applicatif.
 * {@link TransactionService} reste ainsi framework-agnostic
 * (compilable et testable sans Spring).
 * <p>
 * Chaque use case est exposé comme un bean distinct, ce qui permet
 * aux adapters entrants d'injecter uniquement l'interface dont ils ont besoin.
 */
@Configuration
public class ApplicationConfig {

    @Bean
    TransactionService transactionService(TransactionRepositoryPort transactionRepository,
                                          AccountPort accountPort) {
        return new TransactionService(transactionRepository, accountPort);
    }

    // ── Aliases explicites (optionnel mais recommandé) ─────────────────
    // Permet à un contrôleur d'injecter DepositUseCase directement
    // sans connaître TransactionService.

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
