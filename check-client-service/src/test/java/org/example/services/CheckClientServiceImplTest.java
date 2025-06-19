package org.example.services;

import org.example.dto.CheckClientRequest;
import org.example.dto.CheckClientResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CheckClientServiceImplTest {

    private final CheckClientServiceImpl checkClientService = new CheckClientServiceImpl();

    @Test
    void shouldReturnBlackListedTrue_whenLastUuidCharIsEven() {
        // Символ '2' → ASCII = 50 → чётный
        UUID clientId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID accountId = UUID.randomUUID();

        CheckClientRequest request = CheckClientRequest.builder()
                .clientId(clientId)
                .accountId(accountId)
                .build();

        CheckClientResponse response = checkClientService.checkClient(request);

        assertTrue(response.isBlackListed());
        assertEquals(clientId, response.getClientId());
        assertEquals(accountId, response.getAccountId());
    }

    @Test
    void shouldReturnBlackListedFalse_whenLastUuidCharIsOdd() {
        // Символ '3' → ASCII = 51 → нечётный
        UUID clientId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        UUID accountId = UUID.randomUUID();

        CheckClientRequest request = CheckClientRequest.builder()
                .clientId(clientId)
                .accountId(accountId)
                .build();

        CheckClientResponse response = checkClientService.checkClient(request);

        assertFalse(response.isBlackListed());
        assertEquals(clientId, response.getClientId());
        assertEquals(accountId, response.getAccountId());
    }
}
