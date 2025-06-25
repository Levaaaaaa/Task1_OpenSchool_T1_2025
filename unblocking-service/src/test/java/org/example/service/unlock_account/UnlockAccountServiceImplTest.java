package org.example.service.unlock_account;

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
class UnlockAccountServiceImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private UnlockAccountServiceImpl unlockAccountService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(unlockAccountService, "BASE_URL", "http://localhost/unlock/account/");
    }

    @Test
    void shouldCallUnlockAccountEndpointSuccessfully() {
        UUID accountId = UUID.randomUUID();

        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

        unlockAccountService.unlockAccount(accountId);

        verify(webClient).put();
        verify(requestBodyUriSpec).uri("http://localhost/unlock/account/" + accountId);
        verify(requestBodySpec).retrieve();
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void shouldLogErrorWhenUnlockFails() {
        UUID accountId = UUID.randomUUID();

        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(new RuntimeException("Connection error")));

        unlockAccountService.unlockAccount(accountId);

        verify(webClient).put();
        verify(requestBodyUriSpec).uri("http://localhost/unlock/account/" + accountId);
        verify(requestBodySpec).retrieve();
        verify(responseSpec).toBodilessEntity();
    }
}
