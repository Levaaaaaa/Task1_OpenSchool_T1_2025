package org.example.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.example.entity.DataSourceErrorLogEntity;
import org.example.exceptions.KafkaSendException;
import org.example.kafka.KafkaSender;
import org.example.repository.DataSourceErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@RequiredArgsConstructor
public class LogDataSourceErrorAspect {
    private final DataSourceErrorLogRepository repository;

    private final KafkaSender kafkaProducer;

//    @AfterThrowing(pointcut = "execution(* com.t1.snezhko.task1..*(..))", throwing = "ex")
    @AfterThrowing(pointcut = "@within(org.example.annotation.LogException)", throwing = "ex")
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
        catch (KafkaSendException | NullPointerException e) {
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
