package com.t1.snezhko.accept_service.block;

import com.github.benmanes.caffeine.cache.Cache;
import com.t1.snezhko.core.transaction.dto.AcceptTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.Deque;

@Service
class CheckBlockedTransactionImpl implements CheckBlockedTransactionService {
    @Autowired
    private Cache<String, Deque<Instant>> cache;

    @Value("${app.transactions.block.ttl}")
    private long ttl;

    @Value("${app.transactions.block.count}")
    private int transactionsCount;

    @Override
    public boolean isBlocked(AcceptTransactionRequest request) {
        String key = generateKey(request);
        Deque<Instant> instants = cache.getIfPresent(key);

        if (instants == null) {
            instants = new ArrayDeque<>();
        }

        Instant threshold = Instant.now().minus(Duration.ofSeconds(ttl));
        instants.removeIf(instant -> instant.isBefore(threshold));

        instants.addFirst(
                request.getTimestamp()
                        .atZone(
                                ZoneId.systemDefault()
                        )
                        .toInstant());
        cache.put(key, instants);
        return instants.size() > transactionsCount;
    }

    private String generateKey(AcceptTransactionRequest request) {
        return new StringBuilder(
                request.getClientId().toString()
                )
                .append(":")
                .append(
                        request.getAccountId().toString()
                )
                .toString();
    }
}
