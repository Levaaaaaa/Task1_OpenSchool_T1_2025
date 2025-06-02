package com.t1.snezhko.task1.core.account.services;

import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.dto.CreateAccountRequest;
import com.t1.snezhko.task1.core.account.dto.AccountResponse;
import com.t1.snezhko.task1.aop.annotations.Cached;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public interface AccountCrudService {
    public AccountResponse createAccount(CreateAccountRequest request);

    @Cached
    public List<AccountResponse> getAllAccounts();

    @Cached
    public AccountResponse getAccountById(UUID id);

    public AccountResponse updateAccountById(UUID id, CreateAccountRequest request);

    public AccountResponse deleteAccountById(UUID id);

    public AccountResponse addAmount(UUID id, BigDecimal added);

    public AccountResponse addFrozenAmount(UUID id, BigDecimal amountToAdd);

    public AccountResponse updateStatus(UUID id, AccountStatus newStatus);

}
