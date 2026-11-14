package com.bank.accountservice.application.ports.outbound;


import com.bank.accountservice.domain.event.AccountOpenedEvent;

public interface AccountEventPublisherPort {
    void publishAccountOpened(AccountOpenedEvent event);
}
