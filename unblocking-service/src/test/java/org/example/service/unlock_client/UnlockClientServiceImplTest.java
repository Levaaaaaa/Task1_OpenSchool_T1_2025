package org.example.service.unlock_client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnlockClientServiceImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private UnlockClientServiceImpl unlockClientService;

    @BeforeEach
    void setUp() {
        // Устанавливаем BASE_URL вручную
        ReflectionTestUtils.setField(unlockClientService, "HOST_NAME", "localhost");
        ReflectionTestUtils.setField(unlockClientService, "BASE_URL", "/unlock/");
    }

    @Test
    void shouldCallUnlockEndpointSuccessfully() {
        UUID clientId = UUID.randomUUID();

        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        //when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

        unlockClientService.unlockClient(clientId);

        verify(webClient).put();
        verify(requestBodyUriSpec).uri("http://localhost/unlock/" + clientId);
//        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void shouldLogErrorIfRequestFails() {
        UUID clientId = UUID.randomUUID();

        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(new RuntimeException("Connection error")));

        unlockClientService.unlockClient(clientId);

        verify(webClient).put();
        verify(requestBodyUriSpec).uri("http://localhost/unlock/" + clientId);
  //      verify(requestHeadersSpec).retrieve();
        verify(responseSpec).toBodilessEntity();
    }
}
