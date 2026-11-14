package com.bank.accountservice.infrastructure.out.messaging;


import com.bank.accountservice.application.ports.outbound.AccountEventPublisherPort;
import com.bank.accountservice.domain.event.AccountOpenedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaAccountEventPublisher implements AccountEventPublisherPort {

    private static final String TOPIC = "account-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishAccountOpened(AccountOpenedEvent event) {
        kafkaTemplate.send(TOPIC, event.accountId().toString(), event);
        log.info("Published AccountOpenedEvent to topic '{}': accountNumber={}, email={}",
                TOPIC, event.accountNumber(), event.customerEmail()
        );
    }
}
