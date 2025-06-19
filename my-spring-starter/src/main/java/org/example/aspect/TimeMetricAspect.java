package org.example.aspect;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.entity.TimeLimitExceedLogEntity;
import org.example.exceptions.KafkaSendException;
import org.example.kafka.KafkaSender;
import org.example.properties.TimeLimitProperties;
import org.example.repository.TimeLimitExceedLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Slf4j
@RequiredArgsConstructor
public class TimeMetricAspect {

//    @Value("${app.method_execution.time_limit}")
    private final TimeLimitProperties timeLimitProperties;

    private final TimeLimitExceedLogRepository repository;

    private final KafkaSender kafkaProducer;

    @Around("@annotation(org.example.annotation.Metric)")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        LocalDateTime startTime = LocalDateTime.now();
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        String message = "Method's " + joinPoint.getSignature().toShortString() + " execution time - " + duration + ". Limit - " + timeLimitProperties.getTimeLimit();
        log.info(message);
        if (duration > timeLimitProperties.getTimeLimit()) {

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
            catch (KafkaSendException | NullPointerException e) {
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
