package com.t1.snezhko.task1.core.transaction.services;

import com.t1.snezhko.task1.aop.annotations.Cached;
import com.t1.snezhko.task1.core.transaction.dto.TransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface TransactionCrudService {
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request);

    @Cached
    public List<TransactionResponse> getAllTransactions();

    @Cached
    public TransactionResponse getTransactionById(Long id);

    @Transactional
    public TransactionResponse deleteTransaction(Long id);
}
