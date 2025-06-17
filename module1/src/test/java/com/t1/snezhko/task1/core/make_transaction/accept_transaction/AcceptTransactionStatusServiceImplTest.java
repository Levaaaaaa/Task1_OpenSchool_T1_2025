package com.t1.snezhko.task1.core.make_transaction.accept_transaction;

import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.dto.AccountResponse;
import com.t1.snezhko.task1.core.account.services.AccountCrudService;
import com.t1.snezhko.task1.core.transaction.TransactionStatus;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.core.transaction.dto.check_status.CheckTransactionStatusResponse;
import com.t1.snezhko.task1.core.transaction.services.crud.TransactionCrudService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcceptTransactionStatusServiceImplTest {

    @InjectMocks
    private AcceptTransactionStatusStatusImpl acceptTransactionStatusService;

    @Mock
    private TransactionCrudService transactionCrudService;

    @Mock
    private
    AccountCrudService accountCrudService;

    private UUID transactionId;
    private UUID producerId;
    private UUID consumerId;
    private BigDecimal amount;

    private TransactionResponse mockTransactionResponse;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        producerId = UUID.randomUUID();
        consumerId = UUID.randomUUID();
        amount = BigDecimal.valueOf(100);

        AccountResponse producer = new AccountResponse();
        producer.setId(producerId);

        AccountResponse consumer = new AccountResponse();
        consumer.setId(consumerId);

        mockTransactionResponse = new TransactionResponse();
        mockTransactionResponse.setTransactionId(transactionId);
        mockTransactionResponse.setProducer(producer);
        mockTransactionResponse.setConsumer(consumer);
        mockTransactionResponse.setAmount(amount);
    }

    @Test
    void shouldAcceptTransaction_whenStatusIsAccepted() {
        // given
        CheckTransactionStatusResponse response = new CheckTransactionStatusResponse();
        response.setTransactionId(transactionId);
        response.setTransactionAmount(amount);
        response.setStatus(TransactionStatus.ACCEPTED);

        when(transactionCrudService.getTransactionById(transactionId)).thenReturn(mockTransactionResponse);

        // when
        TransactionResponse result = acceptTransactionStatusService.acceptStatus(response);

        // then
        assertEquals(TransactionStatus.ACCEPTED, result.getStatus());
        verify(transactionCrudService).updateTransactionStatus(transactionId, TransactionStatus.ACCEPTED);
        verify(accountCrudService).addAmount(consumerId, amount);
    }

    @Test
    void shouldRejectTransaction_whenStatusIsRejected() {
        CheckTransactionStatusResponse response = new CheckTransactionStatusResponse();
        response.setTransactionId(transactionId);
        response.setTransactionAmount(amount);
        response.setStatus(TransactionStatus.REJECTED);

        when(transactionCrudService.getTransactionById(transactionId)).thenReturn(mockTransactionResponse);

        TransactionResponse result = acceptTransactionStatusService.acceptStatus(response);

        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        verify(transactionCrudService).updateTransactionStatus(transactionId, TransactionStatus.REJECTED);
        verify(accountCrudService).addAmount(producerId, amount);
    }

    @Test
    void shouldBlockTransaction_whenStatusIsBlocked() {
        CheckTransactionStatusResponse response = new CheckTransactionStatusResponse();
        response.setTransactionId(transactionId);
        response.setTransactionAmount(amount);
        response.setStatus(TransactionStatus.BLOCKED);

        when(transactionCrudService.getTransactionById(transactionId)).thenReturn(mockTransactionResponse);

        TransactionResponse result = acceptTransactionStatusService.acceptStatus(response);

        assertEquals(TransactionStatus.BLOCKED, result.getStatus());
        verify(transactionCrudService).updateTransactionStatus(transactionId, TransactionStatus.BLOCKED);
        verify(accountCrudService).updateStatus(producerId, AccountStatus.BLOCKED);
        verify(accountCrudService).addFrozenAmount(producerId, amount);
    }

//    @Test
//    void shouldNotFail_whenUnknownStatusProvided() {
//        CheckTransactionStatusResponse response = new CheckTransactionStatusResponse();
//        response.setTransactionId(transactionId);
//        response.setTransactionAmount(amount);
//        response.setStatus(null); // неизвестный статус
//
//        when(transactionCrudService.getTransactionById(transactionId)).thenReturn(mockTransactionResponse);
//
//        TransactionResponse result = acceptTransactionStatusService.acceptStatus(response);
//
//        // ничего не должно меняться
//        assertNull(result.getStatus());
//        verify(transactionCrudService, never()).updateTransactionStatus(any(), any());
//        verify(accountCrudService, never()).addAmount(any(), any());
//        verify(accountCrudService, never()).updateStatus(any(), any());
//        verify(accountCrudService, never()).addFrozenAmount(any(), any());
//    }

    @Test
    void shouldThrow_whenTransactionNotFound() {
        CheckTransactionStatusResponse response = new CheckTransactionStatusResponse();
        response.setTransactionId(transactionId);
        response.setTransactionAmount(amount);
        response.setStatus(TransactionStatus.ACCEPTED);

        when(transactionCrudService.getTransactionById(transactionId))
                .thenThrow(new EntityNotFoundException("Transaction not found"));

        assertThrows(EntityNotFoundException.class, () ->
                acceptTransactionStatusService.acceptStatus(response));
    }

    @Test
    void shouldHandleZeroAmountGracefully() {
        CheckTransactionStatusResponse response = new CheckTransactionStatusResponse();
        response.setTransactionId(transactionId);
        response.setTransactionAmount(BigDecimal.ZERO);
        response.setStatus(TransactionStatus.ACCEPTED);

        when(transactionCrudService.getTransactionById(transactionId)).thenReturn(mockTransactionResponse);

        TransactionResponse result = acceptTransactionStatusService.acceptStatus(response);

        assertEquals(TransactionStatus.ACCEPTED, result.getStatus());
        verify(accountCrudService).addAmount(consumerId, BigDecimal.ZERO);
    }
}
