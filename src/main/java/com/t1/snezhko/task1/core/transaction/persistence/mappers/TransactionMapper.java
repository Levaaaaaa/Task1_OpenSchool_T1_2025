package com.t1.snezhko.task1.core.transaction.persistence.mappers;

import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.core.transaction.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public interface TransactionMapper {
    public TransactionEntity fromDto(CreateTransactionRequest request);
    public TransactionResponse toDto(TransactionEntity entity);
}
