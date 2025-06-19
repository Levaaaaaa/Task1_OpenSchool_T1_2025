package com.t1.snezhko.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaMessageProducerTest {

    @InjectMocks
    private KafkaMessageProducer kafkaMessageProducer;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldSendMessageToTopicSuccessfully() {
        String topic = "test-topic";
        String message = "hello";

        kafkaMessageProducer.send(topic, message);

        verify(kafkaTemplate).send(topic, message);
    }

    @Test
    void shouldSendSpringMessageSuccessfully() {
        String topic = "test-topic";
        String payload = "payload";
        Message<String> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(message)).thenReturn(future);

        kafkaMessageProducer.send(message);

        verify(kafkaTemplate).send(message);
    }

//    @Test
//    void shouldThrowKafkaSendExceptionIfSendFails() {
//        String topic = "test-topic";
//        String payload = "payload";
//        Message<String> message = MessageBuilder
//                .withPayload(payload)
//                .setHeader(KafkaHeaders.TOPIC, topic)
//                .build();
//
//        CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
//        failedFuture.completeExceptionally(new RuntimeException("Kafka error"));
//
//        when(kafkaTemplate.send(message)).thenReturn(failedFuture);
//
//        KafkaSendException ex = assertThrows(KafkaSendException.class, () -> {
//            kafkaMessageProducer.send(message);
//        });
//
//        assertTrue(ex.getMessage().contains("Error with sending message by Kafka"));
//        verify(kafkaTemplate).send(message);
//    }
}
