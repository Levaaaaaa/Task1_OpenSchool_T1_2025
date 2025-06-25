package org.example.service.org.example.integration;


import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UnlockControllerWireMockIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Запускаем WireMock на 8089 порту
    @RegisterExtension
    static WireMockExtension wireMockRule = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8089))
            .build();

    @BeforeEach
    void setup() {
        wireMockRule.resetAll();
    }

    @Test
    void unlockClient_shouldReturnOk_whenExternalServiceRespondsSuccessfully() throws Exception {
        UUID clientId = UUID.randomUUID();

        // Настраиваем WireMock на имитацию успешного ответа
        wireMockRule.stubFor(
                com.github.tomakehurst.wiremock.client.WireMock.put(
                                com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/unlock/client/" + clientId))
                        .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse().withStatus(200))
        );


        mockMvc.perform(put("/unlock/client/" + clientId.toString()))
                .andExpect(status().isOk());

        // Проверяем, что запрос к WireMock действительно был
        wireMockRule.verify(putRequestedFor(urlEqualTo("/unlock/client/" + clientId)));
    }

    @Test
    void unlockAccount_shouldReturnOk_whenExternalServiceRespondsSuccessfully() throws Exception {
        UUID accountId = UUID.randomUUID();

        wireMockRule.stubFor(
                com.github.tomakehurst.wiremock.client.WireMock.put(
                                com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo("/unlock/account/" + accountId))
                        .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse().withStatus(200))
        );

        mockMvc.perform(put("/unlock/account/{id}", accountId.toString()))
                .andExpect(status().isOk());

        wireMockRule.verify(putRequestedFor(urlEqualTo("/unlock/account/" + accountId)));
    }

    @Test
    void unlockClient_shouldReturnBadRequest_whenInvalidUUID() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(put("/unlock/client/{id}", invalidId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unlockAccount_shouldReturnBadRequest_whenInvalidUUID() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(put("/unlock/account/{id}", invalidId))
                .andExpect(status().isBadRequest());
    }

    // Дополнительно: можно добавить сценарий, когда внешний сервис возвращает ошибку (например, 500)
    @Test
    void unlockClient_shouldReturnOkEvenIfExternalServiceFails() throws Exception {
        UUID clientId = UUID.randomUUID();

        wireMockRule.stubFor(com.github.tomakehurst.wiremock.client.WireMock.put(urlEqualTo("/unlock/client/" + clientId))
                .willReturn(aResponse().withStatus(500)));

        // Наш контроллер всегда возвращает 200 OK, даже если внешний сервис упал
        mockMvc.perform(put("/unlock/client/{id}", clientId.toString()))
                .andExpect(status().isOk());

        wireMockRule.verify(putRequestedFor(urlEqualTo("/unlock/client/" + clientId)));
    }
}
