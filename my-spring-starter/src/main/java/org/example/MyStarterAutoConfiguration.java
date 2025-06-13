package org.example;

import jakarta.persistence.Entity;
import org.example.aspect.LogDataSourceErrorAspect;
import org.example.aspect.TimeMetricAspect;
import org.example.kafka.KafkaSender;
import org.example.properties.TimeLimitProperties;
import org.example.repository.DataSourceErrorLogRepository;
import org.example.repository.TimeLimitExceedLogRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnClass({DataSourceErrorLogRepository.class, TimeLimitExceedLogRepository.class})
@ConditionalOnBean(KafkaSender.class)
@EnableConfigurationProperties(TimeLimitProperties.class)
public class MyStarterAutoConfiguration {

    @Bean
    public LogDataSourceErrorAspect logDataSourceErrorAspect(DataSourceErrorLogRepository dataSourceErrorLogRepository, KafkaSender kafkaSender) {
        return new LogDataSourceErrorAspect(dataSourceErrorLogRepository, kafkaSender);
    }

    @Bean
    public TimeMetricAspect timeMetricAspect(TimeLimitProperties timeLimitProperties, TimeLimitExceedLogRepository timeLimitExceedLogRepository, KafkaSender kafkaSender) {
        return new TimeMetricAspect(timeLimitProperties, timeLimitExceedLogRepository, kafkaSender);
    }
}
