package com.t1.snezhko.task1.core.make_transaction.unlock;

import com.t1.snezhko.task1.core.client.ClientStatus;
import com.t1.snezhko.task1.core.client.persistence.entity.ClientEntity;
import com.t1.snezhko.task1.core.client.persistence.repositories.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private WebClient webClient;

    private static final String UNLOCK_SERVICE_HOST = "t1-unlock-service:8080";
    private static final String UNLOCK_CLIENT_URL = "/unlock/client/";

    private static final int PERIOD = 120;
    private static final int CLIENT_TO_UNLOCK_COUNT = 2;
    private static final int ACCOUNT_TO_UNLOCK_COUNT = 2;

    @Scheduled(fixedDelay = (PERIOD * 1000))
    public void unlockClientAndAccount() {
        List<ClientEntity> clientsToUnlock = clientRepository.findByStatus(ClientStatus.BLOCKED, Pageable.ofSize(CLIENT_TO_UNLOCK_COUNT));
        for (ClientEntity client : clientsToUnlock) {
            sendClientUnlockRequest(client.getClientId());
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
}
