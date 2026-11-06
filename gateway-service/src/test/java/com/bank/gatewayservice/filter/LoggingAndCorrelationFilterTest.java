package com.bank.gatewayservice.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingAndCorrelationFilterTest {

    private LoggingAndCorrelationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new LoggingAndCorrelationFilter();
    }

    @Test
    @DisplayName("Should generate X-Correlation-Id when none is provided in request")
    void shouldGenerateCorrelationIdWhenMissing() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/customers")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        AtomicBoolean downstreamCalled = new AtomicBoolean(false);
        GatewayFilterChain chain = mutatedExchange -> {
            downstreamCalled.set(true);
            // Verify downstream request contains the generated correlation ID
            String header = mutatedExchange.getRequest().getHeaders().getFirst(LoggingAndCorrelationFilter.CORRELATION_ID_HEADER);
            assertThat(header).isNotBlank();
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertThat(downstreamCalled.get()).isTrue();
        // Verify response header also contains the generated correlation ID
        String responseHeader = exchange.getResponse().getHeaders().getFirst(LoggingAndCorrelationFilter.CORRELATION_ID_HEADER);
        assertThat(responseHeader).isNotBlank();
    }

    @Test
    @DisplayName("Should preserve existing X-Correlation-Id from client request")
    void shouldPreserveExistingCorrelationId() {
        String existingId = "custom-test-correlation-id-12345";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/accounts")
                .header(LoggingAndCorrelationFilter.CORRELATION_ID_HEADER, existingId)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = mutatedExchange -> {
            String header = mutatedExchange.getRequest().getHeaders().getFirst(LoggingAndCorrelationFilter.CORRELATION_ID_HEADER);
            assertThat(header).isEqualTo(existingId);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        String responseHeader = exchange.getResponse().getHeaders().getFirst(LoggingAndCorrelationFilter.CORRELATION_ID_HEADER);
        assertThat(responseHeader).isEqualTo(existingId);
    }
}

