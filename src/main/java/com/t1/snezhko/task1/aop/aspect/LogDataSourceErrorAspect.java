package com.t1.snezhko.task1.aop.aspect;

import com.t1.snezhko.task1.core.errorlog.entity.DataSourceErrorLogEntity;
import com.t1.snezhko.task1.core.errorlog.repository.DataSourceErrorLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogDataSourceErrorAspect {
    @Autowired
    private DataSourceErrorLogRepository repository;

//    @AfterThrowing(pointcut = "execution(* com.t1.snezhko.task1..*(..))", throwing = "ex")
    @AfterThrowing(pointcut = "@within(com.t1.snezhko.task1.aop.annotations.LogException)", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        DataSourceErrorLogEntity entity = DataSourceErrorLogEntity.builder()
                .message(ex.getMessage())
                .methodSignature(joinPoint.getSignature().toShortString())
                .stackTrace(getStackTraceAsString(ex))
                .build();
        repository.save(entity);
        log.info("Exception " + ex.getMessage() + " was caught and saved!");
    }

    private String getStackTraceAsString(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

}
