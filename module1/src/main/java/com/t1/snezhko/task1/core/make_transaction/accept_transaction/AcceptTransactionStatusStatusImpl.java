package com.t1.snezhko.task1.core.make_transaction.accept_transaction;

import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.services.AccountCrudService;
import com.t1.snezhko.task1.core.transaction.TransactionStatus;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.core.transaction.dto.check_status.CheckTransactionStatusResponse;
import com.t1.snezhko.task1.core.transaction.services.crud.TransactionCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
class AcceptTransactionStatusStatusImpl implements AcceptTransactionStatusService {
    @Autowired
    private TransactionCrudService transactionCrudService;

    @Autowired
    private AccountCrudService accountCrudService;

    @Override
    @Transactional
    public TransactionResponse acceptStatus(CheckTransactionStatusResponse response) {
        TransactionResponse transactionResponse = transactionCrudService.getTransactionById(response.getTransactionId());
        UUID producerId = transactionResponse.getProducer().getId();
        UUID consumerId = transactionResponse.getConsumer().getId();
        if (response.getStatus().equals(TransactionStatus.ACCEPTED)) {
            transactionCrudService.updateTransactionStatus(response.getTransactionId(), TransactionStatus.ACCEPTED);
            accountCrudService.addAmount(consumerId, response.getTransactionAmount());
            transactionResponse.setStatus(TransactionStatus.ACCEPTED);
        }
        else if (response.getStatus().equals(TransactionStatus.REJECTED)) {
            transactionCrudService.updateTransactionStatus(response.getTransactionId(), TransactionStatus.REJECTED);
            accountCrudService.addAmount(producerId, response.getTransactionAmount());
            transactionResponse.setStatus(TransactionStatus.REJECTED);
        }
        else if (response.getStatus().equals(TransactionStatus.BLOCKED)) {
            transactionCrudService.updateTransactionStatus(response.getTransactionId(), TransactionStatus.BLOCKED);
            accountCrudService.updateStatus(producerId, AccountStatus.BLOCKED);
            accountCrudService.addFrozenAmount(producerId, response.getTransactionAmount());
            transactionResponse.setStatus(TransactionStatus.BLOCKED);
        }

        return transactionResponse;
    }
}
