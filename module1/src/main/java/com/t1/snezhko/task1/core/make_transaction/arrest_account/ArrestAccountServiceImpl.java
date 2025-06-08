package com.t1.snezhko.task1.core.make_transaction.arrest_account;

import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.dto.AccountResponse;
import com.t1.snezhko.task1.core.account.services.AccountCrudService;
import com.t1.snezhko.task1.core.client.dto.ClientDTO;
import com.t1.snezhko.task1.core.transaction.TransactionStatus;
import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
class ArrestAccountServiceImpl implements ArrestAccountService{

    //client -> rejects count
    private Map<UUID, Integer> rejects;

    public ArrestAccountServiceImpl() {
        rejects = new HashMap<>();
    }

    @Value("${app.transactions.reject-count-for-arrest}")
    private int maxNonRejectTransactionCount;

    @Autowired
    private AccountCrudService accountCrudService;

    @Override
    public TransactionResponse checkAccountAndArrest(ClientDTO client, TransactionResponse response) {

        AccountResponse account = response.getProducer();
        log.info("Start transaction checking and arresting service for account " + account.getId() + ", transaction - " + response.getTransactionId() + " Amount - " + response.getAmount() + " Max rejected transaction count - " + maxNonRejectTransactionCount);
        Integer rejectCount = rejects.get(client.getClientId());
        //Если статус клиента при получении сообщения о транзакции известен и транзакции в статусе REJECTED уже существуют
        if (client.getClientStatus() != null
                && rejectCount != null
                && rejectCount.compareTo(maxNonRejectTransactionCount) > 0
        ) {
            /*
            Если статус клиента при получении сообщения о транзакции известен
            и транзакции в статусе REJECTED уже существуют,
            то если приходит более N (значение настраивается в конфиге) транзакций,
            по-прежнему выставить их в REJECTED
            */
            log.info("Arrest account " + account.getId());
            response.setStatus(TransactionStatus.REJECTED);
            accountCrudService.updateStatus(account.getId(), AccountStatus.ARRESTED);
            log.info("Account " + account.getId() + " was arrested successfully!");
            return response;
        }

        log.info("Account " + account.getId() + " wasn't arrested!");
        return response;
    }
}
