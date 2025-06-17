package org.example.kafka;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public interface KafkaSender {
    public void send(Message<String> message);
}
