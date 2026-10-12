package com.bank.accountservice.infra.out.persistence;

import com.bank.accountservice.domain.model.AccountStatus;
import com.bank.accountservice.domain.model.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class AccountJpaEntity {
    @Id
    private UUID id;
    private String accountNumber;
    private UUID customerId;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private BigDecimal balance;
}
