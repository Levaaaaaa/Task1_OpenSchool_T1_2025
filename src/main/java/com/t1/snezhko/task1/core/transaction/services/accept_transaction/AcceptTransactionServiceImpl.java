package com.t1.snezhko.task1.core.transaction.services.accept_transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t1.snezhko.task1.core.transaction.dto.AcceptTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.core.transaction.dto.serializers.AcceptTransactionRequestSerializer;
import com.t1.snezhko.task1.core.transaction.services.TransactionCrudService;
import com.t1.snezhko.task1.kafka.KafkaMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class AcceptTransactionServiceImpl implements AcceptTransactionService{
    @Autowired
    private AcceptTransactionRequestSerializer acceptRequestSerializer;

    @Autowired
    private KafkaMessageProducer kafkaProducer;

    @Autowired
    private TransactionCrudService transactionCrudService;

    private static final String TRANSACTION_ACCEPT_TOPIC = "t1_demo_transaction_accept";

    public TransactionResponse acceptTransaction(CreateTransactionRequest request) throws JsonProcessingException{
        //create entity in db
        TransactionResponse response = transactionCrudService.createTransaction(request);

        sendAcceptRequestToKafka(
                AcceptTransactionRequest.builder()
                        .transactionId(response.getTransactionId()
                        )
                        .transactionAmount(response.getAmount())
                        .accountBalance(response.getProducer().getAmount())
                        .clientId(response.getProducer().getClient().getClientId())
                        .accountId(response.getProducer().getId())
                        .timestamp(response.getTransactionDate())
                        .build()
        );
        return response;
    }

    private void sendAcceptRequestToKafka(AcceptTransactionRequest request) throws JsonProcessingException {
        String payload = acceptRequestSerializer.serialize(request);
        kafkaProducer.send(MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, TRANSACTION_ACCEPT_TOPIC)
                .build()
        );
        log.info("Message " + payload + "was sent into topic " + TRANSACTION_ACCEPT_TOPIC);
    }
}
