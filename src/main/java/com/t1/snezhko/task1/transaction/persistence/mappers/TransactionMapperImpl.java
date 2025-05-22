package com.t1.snezhko.task1.transaction.persistence.mappers;

import com.t1.snezhko.task1.account.persistence.mappers.AccountMapper;
import com.t1.snezhko.task1.transaction.dto.TransactionRequest;
import com.t1.snezhko.task1.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.transaction.persistence.entity.TransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TransactionMapperImpl implements TransactionMapper{
    @Autowired
    private AccountMapper accountMapper;

    @Override
    public TransactionEntity fromDto(TransactionRequest request) {
        return TransactionEntity.builder()
                .amount(request.getAmount())
                .build();
    }

    @Override
    public TransactionResponse toDto(TransactionEntity entity) {
        return TransactionResponse.builder()
                .id(entity.getId())
                .consumer(accountMapper.toDto(entity.getConsumer()))
                .producer(accountMapper.toDto(entity.getProducer()))
                .amount(entity.getAmount())
                .transactionDate(entity.getTransactionDate())
                .build();
    }
}
