package com.t1.snezhko.task1.core.account.services;

import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.dto.CreateAccountRequest;
import com.t1.snezhko.task1.core.account.dto.AccountResponse;
import com.t1.snezhko.task1.core.account.persistence.entity.AccountEntity;
import com.t1.snezhko.task1.core.account.persistence.mappers.AccountMapper;
import com.t1.snezhko.task1.core.account.persistence.repositories.AccountRepository;
import com.t1.snezhko.task1.aop.annotations.Cached;
import com.t1.snezhko.task1.core.client.persistence.entity.ClientEntity;
import com.t1.snezhko.task1.core.client.persistence.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
class AccountCrudServiceImpl implements AccountCrudService{
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountMapper accountMapper;

    //create
    public AccountResponse createAccount(CreateAccountRequest request) {
        Optional<ClientEntity> optional = clientRepository.findByClientId(request.getClientId());
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Client not exists!");
        }
        ClientEntity clientEntity = optional.get();
        AccountEntity accountEntity = accountMapper.fromDto(request);
        accountEntity.setClient(clientEntity);
        accountEntity.setStatus(AccountStatus.OPEN);
        accountEntity.setFrozenAmount(BigDecimal.ZERO);
        accountEntity.setAccountId(UUID.randomUUID());
        accountRepository.save(accountEntity);
        return accountMapper.toDto(accountEntity);
     }
    //read
    @Cached
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream().map(accountMapper::toDto).toList();
    }

    @Cached
    public AccountResponse getAccountById(UUID id) {
        Optional<AccountEntity> optional = accountRepository.findByAccountId(id);
        if (optional.isPresent()) {
            return accountMapper.toDto(optional.get());
        }
        throw new EntityNotFoundException("Account not found!");
    }
    //update
    public AccountResponse updateAccountById(UUID id, CreateAccountRequest request) {
        Optional<ClientEntity> optional = clientRepository.findByClientId(request.getClientId());
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Client not exists!");
        }
        ClientEntity clientEntity = optional.get();
        Optional<AccountEntity> optionalAccount = accountRepository.findByAccountId(id);
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("Account not exists!");
        }
        AccountEntity accountEntity = optionalAccount.get();
        accountEntity.setAccountType(request.getAccountType());
        accountEntity.setAmount(request.getAmount());
        accountEntity.setClient(clientEntity);
        accountRepository.save(accountEntity);
        return accountMapper.toDto(accountEntity);
    }
    //delete
    public AccountResponse deleteAccountById(UUID id) {
        Optional<AccountEntity> optional = accountRepository.findByAccountId(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Account not exists!");
        }
        AccountEntity accountEntity = optional.get();
        accountRepository.delete(accountEntity);
        return accountMapper.toDto(accountEntity);
    }
}
