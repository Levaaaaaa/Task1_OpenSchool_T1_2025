package com.t1.snezhko.task1.aop.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CacheAspect {
    @Autowired
    private Cache<String, Object> cache;

    @Around("@annotation(com.t1.snezhko.task1.aop.annotations.Cached)")
    public Object writeFromTtlCache(ProceedingJoinPoint joinPoint) throws Throwable{
        String key = generateKey(joinPoint);
        Object cached = cache.getIfPresent(key);
        if (cached != null) {
            log.info("Result of method " + joinPoint.getSignature().toShortString() + " was extracted from cache!");
            return cached;
        }

        log.info("Data wasn't found in cache!");
        Object result = joinPoint.proceed();
        cache.put(key, result);
        log.info("Result of method " + joinPoint.getSignature().toShortString() + " was added into cache!");
        return result;
    }

    private String generateKey(ProceedingJoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder(joinPoint.getSignature().toShortString());
        for (Object arg : joinPoint.getArgs()) {
            sb.append(":").append(arg == null ? "null" : arg.toString());
        }
        return sb.toString();
    }
}
