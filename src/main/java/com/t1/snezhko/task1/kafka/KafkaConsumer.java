package com.t1.snezhko.task1.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.snezhko.task1.core.transaction.dto.AcceptTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.services.accept_transaction.AcceptTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {

    @Autowired
    private AcceptTransactionService acceptTransactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "t1_demo_transactions", groupId = "t1-consumer-group")
    public void listenTransactionTopic(@Payload String payload,
                                                           @Headers Map<String, Object> headers)
            throws JsonProcessingException {
        log.info("Receive message " + payload + " from topic t1_demo_transactions");
        acceptTransactionService.acceptTransaction(objectMapper.readValue(payload, CreateTransactionRequest.class));
    }
}
