package org.example.service.unlock_client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class UnlockClientServiceImpl implements UnlockClientService{
    @Autowired
    private final WebClient webClient;

    @Value("${app.unlock.url.client-url}")
    private String BASE_URL;


    @Override
    public void unlockClient(UUID clientId) {
        webClient.put()
                .uri(BASE_URL + clientId.toString())
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.info("Client " + clientId.toString() + " was unlocked successfully! Response status - " + response.getStatusCode()),
                        error -> log.error("When unlocking client " + clientId.toString() + " something went wrong! Error message - " + error.getMessage())
                );
    }
}
