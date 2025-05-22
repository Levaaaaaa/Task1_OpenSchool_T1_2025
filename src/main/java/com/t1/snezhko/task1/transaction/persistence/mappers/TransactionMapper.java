package com.t1.snezhko.task1.transaction.persistence.mappers;

import com.t1.snezhko.task1.transaction.dto.TransactionRequest;
import com.t1.snezhko.task1.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.transaction.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public interface TransactionMapper {
    public TransactionEntity fromDto(TransactionRequest request);
    public TransactionResponse toDto(TransactionEntity entity);
}
