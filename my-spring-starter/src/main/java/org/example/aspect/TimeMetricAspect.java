package com.t1.snezhko.task1.aop.aspect;

import com.t1.snezhko.task1.kafka.KafkaSendException;
import com.t1.snezhko.task1.kafka.KafkaProducer;
import com.t1.snezhko.task1.core.measure.repositories.TimeLimitExceedLogRepository;
import com.t1.snezhko.task1.core.measure.entities.TimeLimitExceedLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class TimeMetricAspect {

    @Value("${app.method_execution.time_limit}")
    private long executionTimeLimit;

    @Autowired
    private TimeLimitExceedLogRepository repository;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Around("@annotation(com.t1.snezhko.task1.aop.annotations.Metric)")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        LocalDateTime startTime = LocalDateTime.now();
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        String message = "Method's " + joinPoint.getSignature().toShortString() + " execution time - " + duration + ". Limit - " + executionTimeLimit;
        log.info(message);
        if (duration > executionTimeLimit) {

            String topic = "t1_demo_metrics";
            try {
                kafkaProducer.send(MessageBuilder
                        .withPayload(message)
                        .setHeader(KafkaHeaders.TOPIC, topic)
                        .setHeader("ERROR_TYPE", "METRICS")
                        .build()
                );
                log.info("Message was sent by Kafka!");
            }
            catch (KafkaSendException e) {
                TimeLimitExceedLogEntity entity = TimeLimitExceedLogEntity.builder()
                    .time(duration)
                    .methodStartTime(startTime)
                    .methodSignature(joinPoint.getSignature().toShortString())
                    .build();
                repository.save(entity);
                log.info("Method's execution time was saved into db!");
            }

        }
        return result;
    }

}
