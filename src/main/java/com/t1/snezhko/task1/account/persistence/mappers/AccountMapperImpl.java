package com.t1.snezhko.task1.account.persistence.mappers;

import com.t1.snezhko.task1.account.dto.AccountRequest;
import com.t1.snezhko.task1.account.dto.AccountResponse;
import com.t1.snezhko.task1.account.persistence.entity.AccountEntity;
import com.t1.snezhko.task1.client.persistence.mappers.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class AccountMapperImpl implements AccountMapper{
    @Autowired
    private ClientMapper clientMapper;
    @Override
    public AccountResponse toDto(AccountEntity entity) {
        return AccountResponse.builder()
                .id(entity.getId())
                .accountType(entity.getAccountType())
                .amount(entity.getAmount())
                .client(clientMapper.toDto(entity.getClient()))
                .build();
    }

    @Override
    public AccountEntity fromDto(AccountRequest request) {
        return AccountEntity.builder()
                .accountType(request.getAccountType())
                .amount(request.getAmount())
                .build();
    }
}
