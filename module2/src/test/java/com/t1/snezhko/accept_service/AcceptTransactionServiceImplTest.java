package com.t1.snezhko.accept_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t1.snezhko.accept_service.block.CheckBlockedTransactionService;
import com.t1.snezhko.accept_service.reject.CheckRejectTransactionService;
import com.t1.snezhko.core.transaction.TransactionStatus;
import com.t1.snezhko.core.transaction.dto.AcceptTransactionRequest;
import com.t1.snezhko.core.transaction.dto.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcceptTransactionServiceImplTest {

    @Mock
    private CheckBlockedTransactionService checkBlockedTransactionService;

    @Mock
    private CheckRejectTransactionService checkRejectTransactionService;

    @InjectMocks
    private AcceptTransactionServiceImpl acceptTransactionService;

    private AcceptTransactionRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = AcceptTransactionRequest.builder()
                .transactionId(UUID.randomUUID())
                .accountId(UUID.randomUUID())
                .transactionAmount(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    void whenTransactionIsBlocked_thenReturnBlockedResponse() throws JsonProcessingException {
        when(checkBlockedTransactionService.isBlocked(request)).thenReturn(true);

        TransactionResponse response = acceptTransactionService.acceptTransaction(request);

        assertEquals(TransactionStatus.BLOCKED, response.getStatus());
        assertEquals(request.getTransactionId(), response.getTransactionId());
        verify(checkBlockedTransactionService).isBlocked(request);
        verifyNoMoreInteractions(checkRejectTransactionService);
    }

    @Test
    void whenTransactionIsRejected_thenReturnRejectedResponse() throws JsonProcessingException {
        when(checkBlockedTransactionService.isBlocked(request)).thenReturn(false);
        when(checkRejectTransactionService.isRejected(request)).thenReturn(true);

        TransactionResponse response = acceptTransactionService.acceptTransaction(request);

        assertEquals(TransactionStatus.REJECTED, response.getStatus());
        assertEquals(request.getTransactionId(), response.getTransactionId());
        verify(checkBlockedTransactionService).isBlocked(request);
        verify(checkRejectTransactionService).isRejected(request);
    }

    @Test
    void whenTransactionIsAccepted_thenReturnAcceptedResponse() throws JsonProcessingException {
        when(checkBlockedTransactionService.isBlocked(request)).thenReturn(false);
        when(checkRejectTransactionService.isRejected(request)).thenReturn(false);

        TransactionResponse response = acceptTransactionService.acceptTransaction(request);

        assertEquals(TransactionStatus.ACCEPTED, response.getStatus());
        assertEquals(request.getTransactionId(), response.getTransactionId());
        verify(checkBlockedTransactionService).isBlocked(request);
        verify(checkRejectTransactionService).isRejected(request);
    }
}
