package com.t1.snezhko.task1.unlock_scheduler;

import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.persistence.entity.AccountEntity;
import com.t1.snezhko.task1.core.account.persistence.repositories.AccountRepository;
import com.t1.snezhko.task1.core.client.ClientStatus;
import com.t1.snezhko.task1.core.client.persistence.entity.ClientEntity;
import com.t1.snezhko.task1.core.client.persistence.repositories.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

//todo remove this bean from test context! Use @Condition

@Component
@Slf4j
public class UnlockTask {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WebClient webClient;

    @Value("${app.unlock.host}")
    private String UNLOCK_SERVICE_HOST;

    @Value("${app.unlock.url.client-url}")
    private String UNLOCK_CLIENT_URL;

    @Value("${app.unlock.url.account-url}")
    private String UNLOCK_ACCOUNT_URL;

    @Value("${app.unlock.limits.clients-count}")
    private int CLIENT_TO_UNLOCK_COUNT;

    @Value("${app.unlock.limits.accounts-count}")
    private int ACCOUNT_TO_UNLOCK_COUNT;

    @Scheduled(fixedDelayString = "${app.unlock.period}")
    public void unlockClientAndAccount() {
        List<ClientEntity> clientsToUnlock = clientRepository.findByStatus(ClientStatus.BLOCKED, Pageable.ofSize(CLIENT_TO_UNLOCK_COUNT));
        for (ClientEntity client : clientsToUnlock) {
            sendClientUnlockRequest(client.getClientId());
        }

        List<AccountEntity> accountsToUnlock = accountRepository.findByStatus(AccountStatus.BLOCKED, Pageable.ofSize(ACCOUNT_TO_UNLOCK_COUNT));
        for (AccountEntity account : accountsToUnlock) {
            sendAccountUnlockRequest(account.getAccountId());
        }
    }

    private void sendClientUnlockRequest(UUID clientId) {
        webClient.put()
                .uri("http://" + UNLOCK_SERVICE_HOST + UNLOCK_CLIENT_URL + clientId.toString())
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.info("Request to unlock client " + clientId.toString() + " was sent successfully!"),
                        error -> log.error("During sending request to unlock client " + clientId.toString() + " something went wrong! Error message: " + error.getMessage())
                );
    }

    private void sendAccountUnlockRequest(UUID accountId) {
        webClient.put()
                .uri("http://" + UNLOCK_SERVICE_HOST + UNLOCK_ACCOUNT_URL + accountId.toString())
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.info("Request to unlock account " + accountId.toString() + " was sent successfully!"),
                        error -> log.error("During sending request to unlock account " + accountId.toString() + " something went wrong! Error message: " + error.getMessage())
                );
    }
}
