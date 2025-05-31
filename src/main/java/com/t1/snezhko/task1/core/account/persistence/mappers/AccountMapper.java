package com.t1.snezhko.task1.core.account.persistence.mappers;

import com.t1.snezhko.task1.core.account.dto.CreateAccountRequest;
import com.t1.snezhko.task1.core.account.dto.AccountResponse;
import com.t1.snezhko.task1.core.account.persistence.entity.AccountEntity;
import org.springframework.stereotype.Service;

@Service
public interface AccountMapper {
    public AccountResponse toDto(AccountEntity entity);
    public AccountEntity fromDto(CreateAccountRequest request);
}
