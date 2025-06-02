package com.t1.snezhko.task1.core.transaction.services.check_status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import org.springframework.stereotype.Service;

@Service
public interface MakeTransactionService {
    public TransactionResponse makeTransaction(CreateTransactionRequest request) throws JsonProcessingException;
}
