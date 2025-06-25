package com.t1.snezhko.task1.prometheus;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class LockMetricService {

    private final Counter blockedClientsCounter;
    private final Counter arrestedAccountsCounter;

    public LockMetricService(MeterRegistry registry) {
        this.blockedClientsCounter = registry.counter("clients.blocked.total");
        this.arrestedAccountsCounter = registry.counter("accounts.arrested.total");
    }

    public void incrementBlockedClients() {
        blockedClientsCounter.increment();
    }

    public void incrementArrestedAccount() {
        arrestedAccountsCounter.increment();
    }

}
