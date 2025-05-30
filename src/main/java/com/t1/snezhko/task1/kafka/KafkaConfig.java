package com.t1.snezhko.task1.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic newTopic() {
        return new NewTopic("t1_demo_metrics", 1, (short)1);
    }
}
