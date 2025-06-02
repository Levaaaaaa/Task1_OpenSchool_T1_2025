package com.t1.snezhko.task1.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void send(Message<String> message) {
        kafkaTemplate.send(message).exceptionally(ex -> {
            throw new KafkaSendException("Error with sending message by Kafka", ex);
        });
    }
}
