package com.t1.snezhko.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.snezhko.accept_service.AcceptTransactionService;
import com.t1.snezhko.core.transaction.dto.AcceptTransactionRequest;
import com.t1.snezhko.core.transaction.dto.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {

    @Autowired
    private KafkaMessageProducer kafkaProducer;

    @Autowired
    private AcceptTransactionService acceptTransactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOPIC_NAME = "t1_demo_transaction_accept";
    private static final String GROUP_ID = "t1-module2-consumer-group";

    private static final String TRANSACTION_RESULT_TOPIC = "t1_demo_transaction_result";

    @KafkaListener(topics = TOPIC_NAME, groupId = GROUP_ID)
    public void listenTransactionTopic(@Payload String payload,
                                                           @Headers Map<String, Object> headers)
            throws JsonProcessingException {
        log.info("Receive message " + payload + " from topic " + TOPIC_NAME);
        TransactionResponse response = acceptTransactionService.acceptTransaction(objectMapper.readValue(payload, AcceptTransactionRequest.class));
        sendAcceptRequestToKafka(TRANSACTION_RESULT_TOPIC, objectMapper.writeValueAsString(response));
    }

    private void sendAcceptRequestToKafka(String topic, String payload) throws JsonProcessingException {;
        kafkaProducer.send(MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build()
        );
        log.info("Message " + payload + "was sent into topic " + topic);
    }
}
