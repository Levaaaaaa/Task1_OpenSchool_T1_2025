package com.t1.snezhko.task1.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.core.transaction.dto.check_status.CheckTransactionStatusResponse;
import com.t1.snezhko.task1.core.transaction.services.accept_transaction.AcceptTransactionStatusService;
import com.t1.snezhko.task1.core.transaction.services.check_status.MakeTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class TransactionKafkaConsumer {

    @Autowired
    private MakeTransactionService makeTransactionService;

    @Autowired
    private AcceptTransactionStatusService acceptTransactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CHECK_TRANSACTION_STATUS_TOPIC_NAME = "t1_demo_transactions";

    private static final String TRANSACTION_RESULT_TOPIC_NAME = "t1_demo_transaction_result";

    @KafkaListener(topics = "t1_demo_transactions", groupId = "t1-consumer-group")
    public void listenTransactionTopic(@Payload String payload,
                                                           @Headers Map<String, Object> headers)
            throws JsonProcessingException {
        log.info("Receive message " + payload + " from topic t1_demo_transactions");
        makeTransactionService.makeTransaction(objectMapper.readValue(payload, CreateTransactionRequest.class));
    }


    @KafkaListener(topics = TRANSACTION_RESULT_TOPIC_NAME, groupId = "t1-transaction-result-consumer-group")
    public void acceptTransaction(@Payload String payload, @Headers Map<String, Object> headers) {
        log.info("Start listener for topic " + TRANSACTION_RESULT_TOPIC_NAME);
        try {
            TransactionResponse response = acceptTransactionService.acceptStatus(objectMapper.readValue(payload, CheckTransactionStatusResponse.class));
            log.info("Receive message " + payload + " from topic " + TRANSACTION_RESULT_TOPIC_NAME + ". Transaction " + response.getTransactionId() + " was assigned the status " + response.getStatus().toString());
        }
        catch (JsonProcessingException e) {
            log.error("During listening topic " + TRANSACTION_RESULT_TOPIC_NAME + " was caught JsonProcessingException: " + e.getMessage());
        }
    }
}
