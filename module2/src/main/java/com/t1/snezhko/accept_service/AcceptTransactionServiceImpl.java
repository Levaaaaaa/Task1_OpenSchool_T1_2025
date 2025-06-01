package com.t1.snezhko.accept_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t1.snezhko.accept_service.block.CheckBlockedTransactionService;
import com.t1.snezhko.accept_service.reject.CheckRejectTransactionService;
import com.t1.snezhko.core.transaction.TransactionStatus;
import com.t1.snezhko.core.transaction.dto.AcceptTransactionRequest;
import com.t1.snezhko.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.kafka.KafkaMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class AcceptTransactionServiceImpl implements AcceptTransactionService {
    @Autowired
    private CheckBlockedTransactionService checkBlockedTransactionService;

    @Autowired
    private CheckRejectTransactionService checkRejectTransactionService;

    public TransactionResponse acceptTransaction(AcceptTransactionRequest request) throws JsonProcessingException{
        if (checkBlockedTransactionService.isBlocked(request)) {
            return buildBlockResponse(request);
        }

        if (checkRejectTransactionService.isRejected(request)) {
            return buildRejectResponse(request);
        }

        return buildAcceptedResponse(request);
    }

    private TransactionResponse buildBlockResponse(AcceptTransactionRequest request) {
        TransactionResponse response = buildAbstractResponse(request);
        response.setStatus(TransactionStatus.BLOCKED);
        return response;
    }

    private TransactionResponse buildRejectResponse(AcceptTransactionRequest request) {
        TransactionResponse response = buildAbstractResponse(request);
        response.setStatus(TransactionStatus.REJECTED);
        return response;
    }

    private TransactionResponse buildAcceptedResponse(AcceptTransactionRequest request) {
        TransactionResponse response = buildAbstractResponse(request);
        response.setStatus(TransactionStatus.ACCEPTED);
        return response;
    }

    private TransactionResponse buildAbstractResponse(AcceptTransactionRequest request) {
        return TransactionResponse.builder()
                .transactionId(request.getTransactionId())
                .producer(request.getAccountId())
                .build();
    }

}
