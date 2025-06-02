package com.t1.snezhko.task1.core.transaction.services.check_status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.check_status.CheckTransactionStatusRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.core.transaction.dto.serializers.AcceptTransactionRequestSerializer;
import com.t1.snezhko.task1.core.transaction.services.crud.TransactionCrudService;
import com.t1.snezhko.task1.kafka.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class MakeTransactionServiceImpl implements MakeTransactionService {
    @Autowired
    private AcceptTransactionRequestSerializer acceptRequestSerializer;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private TransactionCrudService transactionCrudService;

    private static final String TRANSACTION_ACCEPT_TOPIC = "t1_demo_transaction_accept";

    public TransactionResponse makeTransaction(CreateTransactionRequest request) throws JsonProcessingException{
        //create entity in db
        TransactionResponse response = transactionCrudService.createTransaction(request);

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
        return response;
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
