package com.t1.snezhko.task1.account.services;

import com.t1.snezhko.task1.account.dto.AccountRequest;
import com.t1.snezhko.task1.account.dto.AccountResponse;
import com.t1.snezhko.task1.aop.annotations.Cached;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountCrudService {
    public AccountResponse createAccount(AccountRequest request);

    @Cached
    public List<AccountResponse> getAllAccounts();

    @Cached
    public AccountResponse getAccountById(Long id);

    public AccountResponse updateAccountById(Long id, AccountRequest request);

    public AccountResponse deleteAccountById(Long id);
}
