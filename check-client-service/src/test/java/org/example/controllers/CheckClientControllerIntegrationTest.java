package org.example.controllers;

import org.example.config.TestSecurityConfig;
import org.example.dto.CheckClientResponse;
import org.example.exception_handler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
class CheckClientControllerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    String baseUrl() {
        return "http://localhost:" + port + "/api/v1/clients/check";
    }

    @Test
    void shouldReturnBlackListedTrue_whenLastUUIDCharIsEven() {
        UUID clientId = UUID.fromString("00000000-0000-0000-0000-000000000002"); // ASCII 2 → even
        UUID accountId = UUID.randomUUID();

        String url = baseUrl() + "?client-id=" + clientId + "&account-id=" + accountId;

        ResponseEntity<CheckClientResponse> response = restTemplate.getForEntity(url, CheckClientResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isBlackListed()).isTrue();
        assertThat(response.getBody().getClientId()).isEqualTo(clientId);
        assertThat(response.getBody().getAccountId()).isEqualTo(accountId);
    }

    @Test
    void shouldReturnBlackListedFalse_whenLastUUIDCharIsOdd() {
        UUID clientId = UUID.fromString("00000000-0000-0000-0000-000000000003"); // ASCII 3 → odd
        UUID accountId = UUID.randomUUID();

        String url = baseUrl() + "?client-id=" + clientId + "&account-id=" + accountId;

        ResponseEntity<CheckClientResponse> response = restTemplate.getForEntity(url, CheckClientResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isBlackListed()).isFalse();
    }

    @Test
    void shouldReturnBadRequest_whenClientIdIsInvalid() {
        String url = baseUrl() + "?client-id=not-a-uuid&account-id=" + UUID.randomUUID();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnBadRequest_whenClientIdIsMissing() {
        String url = baseUrl() + "?account-id=" + UUID.randomUUID();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnBadRequest_whenAccountIdIsMissing() {
        String url = baseUrl() + "?client-id=" + UUID.randomUUID();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldHandleVeryLongUUIDsGracefully() {
        String longUUID = "123e4567-e89b-12d3-a456-42661417400000000000000000";
        String url = baseUrl() + "?client-id=" + longUUID + "&account-id=" + UUID.randomUUID();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
