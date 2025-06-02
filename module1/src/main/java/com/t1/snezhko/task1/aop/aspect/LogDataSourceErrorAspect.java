package com.t1.snezhko.task1.aop.aspect;

import com.t1.snezhko.task1.core.errorlog.entity.DataSourceErrorLogEntity;
import com.t1.snezhko.task1.core.errorlog.repository.DataSourceErrorLogRepository;
import com.t1.snezhko.task1.kafka.KafkaSendException;
import com.t1.snezhko.task1.kafka.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogDataSourceErrorAspect {
    @Autowired
    private DataSourceErrorLogRepository repository;

    @Autowired
    private KafkaProducer kafkaProducer;

//    @AfterThrowing(pointcut = "execution(* com.t1.snezhko.task1..*(..))", throwing = "ex")
    @AfterThrowing(pointcut = "@within(com.t1.snezhko.task1.aop.annotations.LogException)", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String message = "Method" + joinPoint.getSignature().toShortString() + " was caught exception " + ex.getCause();
        String topic = "t1_demo_metrics";
        log.info(message);
        try {
            kafkaProducer.send(MessageBuilder
                    .withPayload(message)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader("ERROR_TYPE", "DATA_SOURCE")
                    .build()
            );
            log.info("Exception " + ex.getMessage() + " was sent into Kafka!");
        }
        catch (KafkaSendException e) {
            DataSourceErrorLogEntity entity = DataSourceErrorLogEntity.builder()
                    .message(ex.getMessage())
                    .methodSignature(joinPoint.getSignature().toShortString())
                    .stackTrace(getStackTraceAsString(ex))
                    .build();
            repository.save(entity);
            log.info("Exception " + ex.getMessage() + " was saved into db!");
        }
    }

    private String getStackTraceAsString(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

}
