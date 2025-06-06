package com.t1.snezhko.task1.core.make_transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.persistence.entity.AccountEntity;
import com.t1.snezhko.task1.core.account.persistence.repositories.AccountRepository;
import com.t1.snezhko.task1.core.account.services.AccountCrudService;
import com.t1.snezhko.task1.core.client.ClientStatus;
import com.t1.snezhko.task1.core.client.dto.ClientDTO;
import com.t1.snezhko.task1.core.client.persistence.entity.ClientEntity;
import com.t1.snezhko.task1.core.client.services.ClientService;
import com.t1.snezhko.task1.core.make_transaction.check_client.CheckClientService;
import com.t1.snezhko.task1.core.make_transaction.dto.CheckClientRequest;
import com.t1.snezhko.task1.core.make_transaction.dto.CheckClientResponse;
import com.t1.snezhko.task1.core.transaction.TransactionStatus;
import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.check_status.CheckTransactionStatusRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.core.transaction.dto.serializers.AcceptTransactionRequestSerializer;
import com.t1.snezhko.task1.core.transaction.services.crud.TransactionCrudService;
import com.t1.snezhko.task1.kafka.KafkaProducer;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
class MakeTransactionServiceImpl implements MakeTransactionService {
    @Autowired
    private AcceptTransactionRequestSerializer acceptRequestSerializer;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private TransactionCrudService transactionCrudService;

    @Autowired
    private CheckClientService checkClientService;

    @Autowired
    private AccountCrudService accountCrudService;

    @Autowired
    private ClientService clientService;

    private static final String TRANSACTION_ACCEPT_TOPIC = "t1_demo_transaction_accept";

    public TransactionResponse makeTransaction(CreateTransactionRequest request) throws JsonProcessingException{
        //create entity in db
        TransactionResponse response = transactionCrudService.createTransaction(request);

        ClientDTO clientDTO = clientService.getClientById(getClientId(request.getProducer()));
        boolean isClientActive = true;
        if (clientDTO.getClientStatus() == null || clientDTO.getClientStatus().equals(ClientStatus.ACTIVE)) {
            isClientActive = checkClientStatus(request, response.getTransactionId());
        }

        if (isClientActive) {
            sendAcceptRequestToKafka(
                    CheckTransactionStatusRequest.builder()
                            .transactionId(response.getTransactionId()
                            )
                            .transactionAmount(response.getAmount())
                            .accountBalance(response.getProducer().getAmount())
                            .clientId(response.getProducer().getClient().getClientId())
                            .accountId(response.getProducer().getId())
                            .timestamp(response.getTransactionDate())
                            .build()
            );
        }
        return response;
    }

    private boolean checkClientStatus(CreateTransactionRequest request, UUID transactionId) {
        CheckClientRequest checkClientRequest = CheckClientRequest.builder()
                .accountId(request.getProducer())
                .clientId(getClientId(request.getProducer()))
                .build();
        CheckClientResponse checkClientResponse = checkClientService.checkClient(checkClientRequest);

        if (checkClientResponse.isBlackListed()) {
            clientService.updateClientStatus(checkClientRequest.getClientId(), ClientStatus.BLOCKED);
            accountCrudService.updateStatus(checkClientRequest.getAccountId(), AccountStatus.BLOCKED);
            transactionCrudService.updateTransactionStatus(transactionId, TransactionStatus.REJECTED);
            log.info("Client " + checkClientRequest.getClientId() + " was BLOCKED!");
            log.info("Account " + checkClientRequest.getAccountId() + " was BLOCKED!");
            log.info("Transaction " + transactionId + " was REJECTED");
            return false;
        }
        return true;
    }

    private UUID getClientId(UUID producerAccountId) {
        return accountCrudService.getAccountById(producerAccountId).getClient().getClientId();
    }

    private void sendAcceptRequestToKafka(CheckTransactionStatusRequest request) throws JsonProcessingException {
        String payload = acceptRequestSerializer.serialize(request);
        kafkaProducer.send(MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, TRANSACTION_ACCEPT_TOPIC)
                .build()
        );
        log.info("Message " + payload + "was sent into topic " + TRANSACTION_ACCEPT_TOPIC);
    }
}
