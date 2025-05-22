package com.t1.snezhko.task1.account.persistence.mappers;

import com.t1.snezhko.task1.account.dto.AccountRequest;
import com.t1.snezhko.task1.account.dto.AccountResponse;
import com.t1.snezhko.task1.account.persistence.entity.AccountEntity;
import org.springframework.stereotype.Service;

@Service
public interface AccountMapper {
    public AccountResponse toDto(AccountEntity entity);
    public AccountEntity fromDto(AccountRequest request);
}
