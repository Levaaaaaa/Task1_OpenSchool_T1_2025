package com.t1.snezhko.accept_service.block;

import com.github.benmanes.caffeine.cache.Cache;
import com.t1.snezhko.core.transaction.dto.AcceptTransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckBlockedTransactionImplTest {

    private CheckBlockedTransactionImpl service;
    private Cache<String, Deque<Instant>> cache;

    private final UUID clientId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();
    private final long ttlSeconds = 60; // TTL = 60 секунд
    private final int transactionLimit = 3;

    @BeforeEach
    void setUp() {
        cache = mock(Cache.class);
        service = new CheckBlockedTransactionImpl();
        ReflectionTestUtils.setField(service, "cache", cache);
        ReflectionTestUtils.setField(service, "ttl", ttlSeconds);
        ReflectionTestUtils.setField(service, "transactionsCount", transactionLimit);
    }

    private AcceptTransactionRequest buildRequestWithTimestamp(LocalDateTime time) {
        return AcceptTransactionRequest.builder()
                .clientId(clientId)
                .accountId(accountId)
                .transactionAmount(null)
                .accountBalance(null)
                .transactionId(UUID.randomUUID())
                .timestamp(time)
                .build();
    }

    @Test
    void whenNoPreviousTransactions_thenNotBlocked() {
        String key = clientId + ":" + accountId;
        when(cache.getIfPresent(key)).thenReturn(null);

        AcceptTransactionRequest request = buildRequestWithTimestamp(LocalDateTime.now());
        boolean blocked = service.isBlocked(request);

        assertFalse(blocked);
        verify(cache).put(eq(key), any());
    }

    @Test
    void whenFewerThanLimitTransactions_thenNotBlocked() {
        String key = clientId + ":" + accountId;

        Instant now = Instant.now();
        Deque<Instant> history = new ArrayDeque<>(List.of(
                now.minusSeconds(10),
                now.minusSeconds(20)
        ));

        when(cache.getIfPresent(key)).thenReturn(history);

        AcceptTransactionRequest request = buildRequestWithTimestamp(LocalDateTime.ofInstant(now, ZoneId.systemDefault()));
        boolean blocked = service.isBlocked(request);

        assertFalse(blocked);
        verify(cache).put(eq(key), any());
    }

    @Test
    void whenEqualToLimitTransactions_thenNotBlocked() {
        String key = clientId + ":" + accountId;

        Instant now = Instant.now();
        Deque<Instant> history = new ArrayDeque<>(List.of(
                now.minusSeconds(10),
                now.minusSeconds(20)
        ));

        when(cache.getIfPresent(key)).thenReturn(history);

        AcceptTransactionRequest request = buildRequestWithTimestamp(LocalDateTime.ofInstant(now, ZoneId.systemDefault()));
        boolean blocked = service.isBlocked(request);

        assertFalse(blocked);
    }

    @Test
    void whenMoreThanLimitTransactions_thenBlocked() {
        String key = clientId + ":" + accountId;

        Instant now = Instant.now();
        Deque<Instant> history = new ArrayDeque<>(List.of(
                now.minusSeconds(10),
                now.minusSeconds(15),
                now.minusSeconds(20),
                now.minusSeconds(25)
        ));

        when(cache.getIfPresent(key)).thenReturn(history);

        AcceptTransactionRequest request = buildRequestWithTimestamp(LocalDateTime.ofInstant(now, ZoneId.systemDefault()));
        boolean blocked = service.isBlocked(request);

        assertTrue(blocked);
    }

    @Test
    void whenOldTransactionsOutsideTTL_thenNotBlocked() {
        String key = clientId + ":" + accountId;

        Instant now = Instant.now();
        Deque<Instant> history = new ArrayDeque<>(List.of(
                now.minusSeconds(100),
                now.minusSeconds(90)
        ));

        when(cache.getIfPresent(key)).thenReturn(history);

        AcceptTransactionRequest request = buildRequestWithTimestamp(LocalDateTime.ofInstant(now, ZoneId.systemDefault()));
        boolean blocked = service.isBlocked(request);

        assertFalse(blocked);
    }

    @Test
    void whenExactlyAtTTL_thenNotRemoved() {
        String key = clientId + ":" + accountId;

        Instant now = Instant.now();
        Deque<Instant> history = new ArrayDeque<>(List.of(
                now.minusSeconds(ttlSeconds)
        ));

        when(cache.getIfPresent(key)).thenReturn(history);

        AcceptTransactionRequest request = buildRequestWithTimestamp(LocalDateTime.ofInstant(now, ZoneId.systemDefault()));
        boolean blocked = service.isBlocked(request);

        assertFalse(blocked); // потому что считается "не старше"
    }
}

