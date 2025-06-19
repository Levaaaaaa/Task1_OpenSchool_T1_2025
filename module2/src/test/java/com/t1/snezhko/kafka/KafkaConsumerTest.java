package com.t1.snezhko.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.snezhko.accept_service.AcceptTransactionService;
import com.t1.snezhko.core.transaction.TransactionStatus;
import com.t1.snezhko.core.transaction.dto.AcceptTransactionRequest;
import com.t1.snezhko.core.transaction.dto.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Mock
    private KafkaMessageProducer kafkaProducer;

    @Mock
    private AcceptTransactionService acceptTransactionService;

    @Mock
    private ObjectMapper objectMapper;

    private final String topicName = "t1_demo_transaction_accept";
    private final String resultTopic = "t1_demo_transaction_result";

    private AcceptTransactionRequest request;
    private TransactionResponse response;

    @BeforeEach
    void setUp() {
        request = AcceptTransactionRequest.builder()
                .transactionId(UUID.randomUUID())
                .accountId(UUID.randomUUID())
                .clientId(UUID.randomUUID())
                .transactionAmount(BigDecimal.valueOf(100))
                .accountBalance(BigDecimal.valueOf(500))
                .timestamp(LocalDateTime.now())
                .build();

        response = TransactionResponse.builder()
                .transactionId(request.getTransactionId())
                .producer(request.getAccountId())
                .transactionAmount(request.getTransactionAmount())
                .status(TransactionStatus.ACCEPTED)
                .build();
    }

    @Test
    void shouldProcessMessageSuccessfully() throws Exception {
        String jsonPayload = "payload json";
        String responseJson = "response json";

        when(objectMapper.readValue(jsonPayload, AcceptTransactionRequest.class)).thenReturn(request);
        when(acceptTransactionService.acceptTransaction(request)).thenReturn(response);
        when(objectMapper.writeValueAsString(response)).thenReturn(responseJson);

        kafkaConsumer.listenTransactionTopic(jsonPayload, Map.of());

        verify(objectMapper).readValue(jsonPayload, AcceptTransactionRequest.class);
        verify(acceptTransactionService).acceptTransaction(request);
        verify(objectMapper).writeValueAsString(response);

        verify(kafkaProducer).send(argThat(msg ->
                msg.getPayload().equals(responseJson)
                        && resultTopic.equals(msg.getHeaders().get(KafkaHeaders.TOPIC))
        ));
    }

    @Test
    void shouldThrowExceptionOnInvalidJson() throws Exception {
        String invalidJson = "{invalid json}";

        when(objectMapper.readValue(invalidJson, AcceptTransactionRequest.class))
                .thenThrow(JsonProcessingException.class);

        assertThrows(JsonProcessingException.class, () -> {
            kafkaConsumer.listenTransactionTopic(invalidJson, Map.of());
        });

        verifyNoInteractions(acceptTransactionService);
        verify(kafkaProducer, never()).send(any());
    }
}
