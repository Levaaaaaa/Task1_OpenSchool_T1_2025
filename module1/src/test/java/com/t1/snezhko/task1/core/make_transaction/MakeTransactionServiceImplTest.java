package com.t1.snezhko.task1.core.make_transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.dto.AccountResponse;
import com.t1.snezhko.task1.core.account.services.AccountCrudService;
import com.t1.snezhko.task1.core.client.ClientStatus;
import com.t1.snezhko.task1.core.client.dto.ClientDTO;
import com.t1.snezhko.task1.core.client.services.ClientService;
import com.t1.snezhko.task1.core.make_transaction.arrest_account.ArrestAccountService;
import com.t1.snezhko.task1.core.make_transaction.check_client.CheckClientService;
import com.t1.snezhko.task1.core.make_transaction.dto.CheckClientResponse;
import com.t1.snezhko.task1.core.transaction.TransactionStatus;
import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.core.transaction.dto.serializers.AcceptTransactionRequestSerializer;
import com.t1.snezhko.task1.core.transaction.services.crud.TransactionCrudService;
import com.t1.snezhko.task1.kafka.KafkaProducer;
import org.apache.kafka.clients.ClientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MakeTransactionServiceImplTest {

    @InjectMocks
    private MakeTransactionServiceImpl makeTransactionService;

    @Mock
    private AcceptTransactionRequestSerializer acceptRequestSerializer;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private TransactionCrudService transactionCrudService;

    @Mock
    private CheckClientService checkClientService;

    @Mock
    private AccountCrudService accountCrudService;

    @Mock
    private ClientService clientService;

    @Mock
    private ArrestAccountService arrestAccountService;

    private CreateTransactionRequest request;
    private TransactionResponse transactionResponse;
    private ClientDTO clientDTO;
    private AccountResponse producerAccount;
    private UUID transactionId;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        UUID producerAccountId = UUID.randomUUID();

        request = new CreateTransactionRequest();
        request.setProducer(producerAccountId);
        request.setAmount(BigDecimal.valueOf(100));

        clientDTO = new ClientDTO();
        clientDTO.setClientId(clientId);
        clientDTO.setClientStatus(ClientStatus.ACTIVE);

        producerAccount = new AccountResponse();
        producerAccount.setId(producerAccountId);
        producerAccount.setClient(clientDTO);
        producerAccount.setAmount(BigDecimal.valueOf(1000));

        transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(transactionId);
        transactionResponse.setProducer(producerAccount);
        transactionResponse.setAmount(BigDecimal.valueOf(100));
        transactionResponse.setStatus(TransactionStatus.REQUESTED);
        transactionResponse.setTransactionDate(LocalDateTime.now());

    }

    @Test
    public void shouldSendKafkaMessage_whenClientIsActiveAndNotBlacklisted() throws Exception {
        when(transactionCrudService.createTransaction(any())).thenReturn(transactionResponse);
        when(accountCrudService.getAccountById(any())).thenReturn(producerAccount);
        when(clientService.getClientById(clientId)).thenReturn(clientDTO);
        when(arrestAccountService.checkAccountAndArrest(eq(clientDTO), any())).thenReturn(transactionResponse);

        CheckClientResponse checkClientResponse = new CheckClientResponse();
        checkClientResponse.setBlackListed(false);
        when(checkClientService.checkClient(any())).thenReturn(checkClientResponse);
        when(acceptRequestSerializer.serialize(any())).thenReturn("serialized-request");

        TransactionResponse result = makeTransactionService.makeTransaction(request);

        assertEquals(TransactionStatus.REQUESTED, result.getStatus());
        verify(kafkaProducer).send(any());
    }

    @Test
    void shouldNotSendKafka_whenClientIsBlacklisted() throws Exception {
        when(transactionCrudService.createTransaction(any())).thenReturn(transactionResponse);
        when(accountCrudService.getAccountById(any())).thenReturn(producerAccount);
        when(clientService.getClientById(clientId)).thenReturn(clientDTO);
        when(arrestAccountService.checkAccountAndArrest(eq(clientDTO), any())).thenReturn(transactionResponse);

        CheckClientResponse checkClientResponse = new CheckClientResponse();
        checkClientResponse.setBlackListed(true);
        when(checkClientService.checkClient(any())).thenReturn(checkClientResponse);

        TransactionResponse result = makeTransactionService.makeTransaction(request);

        assertEquals(TransactionStatus.REQUESTED, result.getStatus()); // updated inside, but returned same object
        verify(transactionCrudService).updateTransactionStatus(transactionId, TransactionStatus.REJECTED);
        verify(accountCrudService).updateStatus(producerAccount.getId(), AccountStatus.BLOCKED);
        verify(clientService).updateClientStatus(clientId, ClientStatus.BLOCKED);
        verify(kafkaProducer, never()).send(any());
    }

    @Test
    void shouldNotCheckClient_whenTransactionArrested() throws Exception {
        transactionResponse.setStatus(TransactionStatus.REJECTED); // имитация ареста

        when(transactionCrudService.createTransaction(any())).thenReturn(transactionResponse);
        when(accountCrudService.getAccountById(any())).thenReturn(producerAccount);
        when(clientService.getClientById(clientId)).thenReturn(clientDTO);
        when(arrestAccountService.checkAccountAndArrest(eq(clientDTO), any())).thenReturn(transactionResponse);

        TransactionResponse result = makeTransactionService.makeTransaction(request);

        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        verify(checkClientService, never()).checkClient(any());
        verify(kafkaProducer, never()).send(any());
    }

    @Test
    void shouldTreatClientAsActive_whenClientStatusIsNull() throws Exception {
        clientDTO.setClientStatus(null);

        when(transactionCrudService.createTransaction(any())).thenReturn(transactionResponse);
        when(accountCrudService.getAccountById(any())).thenReturn(producerAccount);
        when(clientService.getClientById(clientId)).thenReturn(clientDTO);
        when(arrestAccountService.checkAccountAndArrest(eq(clientDTO), any())).thenReturn(transactionResponse);

        CheckClientResponse checkClientResponse = new CheckClientResponse();
        checkClientResponse.setBlackListed(false);
        when(checkClientService.checkClient(any())).thenReturn(checkClientResponse);
        when(acceptRequestSerializer.serialize(any())).thenReturn("serialized");

        TransactionResponse result = makeTransactionService.makeTransaction(request);

        assertEquals(TransactionStatus.REQUESTED, result.getStatus());
        verify(kafkaProducer).send(any());
    }

    @Test
    void shouldThrowException_whenSerializationFails() throws Exception {
        when(transactionCrudService.createTransaction(any())).thenReturn(transactionResponse);
        when(accountCrudService.getAccountById(any())).thenReturn(producerAccount);
        when(clientService.getClientById(clientId)).thenReturn(clientDTO);
        when(arrestAccountService.checkAccountAndArrest(eq(clientDTO), any())).thenReturn(transactionResponse);

        CheckClientResponse checkClientResponse = new CheckClientResponse();
        checkClientResponse.setBlackListed(false);
        when(checkClientService.checkClient(any())).thenReturn(checkClientResponse);

        when(acceptRequestSerializer.serialize(any())).thenThrow(new JsonProcessingException("error") {});

        assertThrows(JsonProcessingException.class, () ->
                makeTransactionService.makeTransaction(request));
    }
}

