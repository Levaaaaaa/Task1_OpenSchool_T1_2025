package com.t1.snezhko.accept_service.reject;

import com.t1.snezhko.core.transaction.dto.AcceptTransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CheckRejectTransactionServiceImplTest {

    private CheckRejectTransactionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CheckRejectTransactionServiceImpl();
    }

    @Test
    void whenTransactionAmountGreaterThanBalance_thenRejected() {
        AcceptTransactionRequest request = AcceptTransactionRequest.builder()
                .transactionAmount(BigDecimal.valueOf(150))
                .accountBalance(BigDecimal.valueOf(100))
                .transactionId(UUID.randomUUID())
                .accountId(UUID.randomUUID())
                .build();

        assertTrue(service.isRejected(request));
    }

    @Test
    void whenTransactionAmountEqualToBalance_thenNotRejected() {
        AcceptTransactionRequest request = AcceptTransactionRequest.builder()
                .transactionAmount(BigDecimal.valueOf(100))
                .accountBalance(BigDecimal.valueOf(100))
                .transactionId(UUID.randomUUID())
                .accountId(UUID.randomUUID())
                .build();

        assertFalse(service.isRejected(request));
    }

    @Test
    void whenTransactionAmountLessThanBalance_thenNotRejected() {
        AcceptTransactionRequest request = AcceptTransactionRequest.builder()
                .transactionAmount(BigDecimal.valueOf(50))
                .accountBalance(BigDecimal.valueOf(100))
                .transactionId(UUID.randomUUID())
                .accountId(UUID.randomUUID())
                .build();

        assertFalse(service.isRejected(request));
    }
}
