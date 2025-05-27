package com.t1.snezhko.task1.aop.aspect;

import com.t1.snezhko.task1.measure.entities.TimeLimitExceedLogEntity;
import com.t1.snezhko.task1.measure.repositories.TimeLimitExceedLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Around("@annotation(com.t1.snezhko.task1.aop.annotations.Metric)")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        LocalDateTime startTime = LocalDateTime.now();
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("Method's " + joinPoint.getSignature().toShortString() + " execution time - " + duration + ". Limit - " + executionTimeLimit);
        if (duration > executionTimeLimit) {
            TimeLimitExceedLogEntity entity = TimeLimitExceedLogEntity.builder()
                    .time(duration)
                    .methodStartTime(startTime)
                    .methodSignature(joinPoint.getSignature().toShortString())
                    .build();
            repository.save(entity);
            log.info("Method's execution time was saved!");
        }
        return result;
    }
}
