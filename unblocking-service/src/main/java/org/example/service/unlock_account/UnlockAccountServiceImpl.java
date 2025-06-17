package org.example.service.unlock_account;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@Slf4j
class UnlockAccountServiceImpl implements UnlockAccountService{

    @Autowired
    private WebClient webClient;

    private final static String BASE_URL = "/api/v1/accounts/unlock/";

    @Override
    public void unlockAccount(UUID accountId) {
        webClient.put()
                .uri(BASE_URL + accountId.toString())
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.info("Account " + accountId.toString() + " was unlocked successfully! Response status - " + response.getStatusCode()),
                        error -> log.error("During unlocking account " + accountId.toString() + " something went wrong! Error message - " + error.getMessage())
                );
    }
}
