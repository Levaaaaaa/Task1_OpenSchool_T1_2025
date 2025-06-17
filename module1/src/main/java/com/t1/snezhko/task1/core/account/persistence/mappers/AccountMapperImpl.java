package com.t1.snezhko.task1.core.account.persistence.mappers;

import com.t1.snezhko.task1.core.account.dto.CreateAccountRequest;
import com.t1.snezhko.task1.core.account.dto.AccountResponse;
import com.t1.snezhko.task1.core.account.persistence.entity.AccountEntity;
import com.t1.snezhko.task1.core.client.persistence.mappers.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class AccountMapperImpl implements AccountMapper{
    @Autowired
    private ClientMapper clientMapper;
    @Override
    public AccountResponse toDto(AccountEntity entity) {
        return AccountResponse.builder()
                .id(entity.getAccountId())
                .status(entity.getStatus())
                .frozenAmount(entity.getFrozenAmount())
                .accountType(entity.getAccountType())
                .amount(entity.getAmount())
                .client(clientMapper.toDto(entity.getClient()))
                .build();
    }

    @Override
    public AccountEntity fromDto(CreateAccountRequest request) {
        return AccountEntity.builder()
                .accountType(request.getAccountType())
                .amount(request.getAmount())
                .build();
    }
}
