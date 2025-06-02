package com.t1.snezhko.accept_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t1.snezhko.core.transaction.dto.AcceptTransactionRequest;
import com.t1.snezhko.core.transaction.dto.TransactionResponse;
import org.springframework.stereotype.Service;

@Service
public interface AcceptTransactionService {
    public TransactionResponse acceptTransaction(AcceptTransactionRequest request) throws JsonProcessingException;
}
