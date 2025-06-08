package com.t1.snezhko.task1.core.make_transaction.arrest_account;

import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.dto.AccountResponse;
import com.t1.snezhko.task1.core.account.services.AccountCrudService;
import com.t1.snezhko.task1.core.client.ClientStatus;
import com.t1.snezhko.task1.core.client.dto.ClientDTO;
import com.t1.snezhko.task1.core.transaction.TransactionStatus;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ArrestAccountServiceImplTest {

    @InjectMocks
    private ArrestAccountServiceImpl arrestAccountService;

    @Mock
    private AccountCrudService accountCrudService;

    @BeforeEach
    void setup() throws Exception {
        // Устанавливаем значение поля через reflection, так как оно инжектится через @Value
        Field field = ArrestAccountServiceImpl.class.getDeclaredField("maxNonRejectTransactionCount");
        field.setAccessible(true);
        field.set(arrestAccountService, 2); // Пример: блокировка после 2 отклонённых транзакций

        // Устанавливаем значение карты через reflection, так как поле приватное
        Field mapField = ArrestAccountServiceImpl.class.getDeclaredField("rejects");
        mapField.setAccessible(true);
        mapField.set(arrestAccountService, new HashMap<>());
    }

    @Test
    void shouldArrestAccount_whenRejectCountExceedsThresholdAndClientStatusIsKnown() {
        // given
        UUID clientId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        ClientDTO client = new ClientDTO();
        client.setClientId(clientId);
        client.setClientStatus(ClientStatus.ACTIVE);

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setId(accountId);

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setProducer(accountResponse);
        transactionResponse.setTransactionId(transactionId);
        transactionResponse.setAmount(BigDecimal.valueOf(100));
        transactionResponse.setStatus(TransactionStatus.REQUESTED); // Исходный статус

        // вручную подставим reject count
        Map<UUID, Integer> rejects = new HashMap<>();
        rejects.put(clientId, 3); // Больше max (2)
        setField(arrestAccountService, "rejects", rejects);

        // when
        TransactionResponse result = arrestAccountService.checkAccountAndArrest(client, transactionResponse);

        // then
        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        verify(accountCrudService).updateStatus(accountId, AccountStatus.ARRESTED);
    }

    @Test
    void shouldNotArrest_whenRejectCountIsBelowThreshold() {
        // given
        UUID clientId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        ClientDTO client = new ClientDTO();
        client.setClientId(clientId);
        client.setClientStatus(ClientStatus.ACTIVE);

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setId(accountId);

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setProducer(accountResponse);
        transactionResponse.setTransactionId(UUID.randomUUID());
        transactionResponse.setAmount(BigDecimal.valueOf(100));
        transactionResponse.setStatus(TransactionStatus.REQUESTED);

        Map<UUID, Integer> rejects = new HashMap<>();
        rejects.put(clientId, 1); // Меньше max (2)
        setField(arrestAccountService, "rejects", rejects);

        // when
        TransactionResponse result = arrestAccountService.checkAccountAndArrest(client, transactionResponse);

        // then
        assertEquals(TransactionStatus.REQUESTED, result.getStatus());
        verifyNoInteractions(accountCrudService);
    }

    @Test
    void shouldNotArrest_whenRejectCountIsNull() {
        // given
        UUID clientId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        ClientDTO client = new ClientDTO();
        client.setClientId(clientId);
        client.setClientStatus(ClientStatus.ACTIVE); // Статус есть

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setId(accountId);

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setProducer(accountResponse);
        transactionResponse.setTransactionId(UUID.randomUUID());
        transactionResponse.setAmount(BigDecimal.valueOf(100));
        transactionResponse.setStatus(TransactionStatus.REQUESTED);

        // rejects не содержит clientId
        setField(arrestAccountService, "rejects", new HashMap<>());

        // when
        TransactionResponse result = arrestAccountService.checkAccountAndArrest(client, transactionResponse);

        // then
        assertEquals(TransactionStatus.REQUESTED, result.getStatus());
        verifyNoInteractions(accountCrudService);
    }

    @Test
    void shouldNotArrest_whenClientStatusIsNull() {
        // given
        UUID clientId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        ClientDTO client = new ClientDTO();
        client.setClientId(clientId);
        client.setClientStatus(null); // Нет статуса

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setId(accountId);

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setProducer(accountResponse);
        transactionResponse.setTransactionId(UUID.randomUUID());
        transactionResponse.setAmount(BigDecimal.valueOf(100));
        transactionResponse.setStatus(TransactionStatus.REQUESTED);

        Map<UUID, Integer> rejects = new HashMap<>();
        rejects.put(clientId, 10); // Больше max, но статус null
        setField(arrestAccountService, "rejects", rejects);

        // when
        TransactionResponse result = arrestAccountService.checkAccountAndArrest(client, transactionResponse);

        // then
        assertEquals(TransactionStatus.REQUESTED, result.getStatus());
        verifyNoInteractions(accountCrudService);
    }

    // Вспомогательный метод для установки приватных полей
    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = ArrestAccountServiceImpl.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
