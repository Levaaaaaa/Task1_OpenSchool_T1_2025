package com.t1.snezhko.task1.account.services;

import com.t1.snezhko.task1.account.AccountType;
import com.t1.snezhko.task1.account.dto.AccountRequest;
import com.t1.snezhko.task1.account.dto.AccountResponse;
import com.t1.snezhko.task1.account.persistence.entity.AccountEntity;
import com.t1.snezhko.task1.account.persistence.mappers.AccountMapper;
import com.t1.snezhko.task1.account.persistence.repositories.AccountRepository;
import com.t1.snezhko.task1.client.persistence.entity.ClientEntity;
import com.t1.snezhko.task1.client.persistence.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountCrudService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountMapper accountMapper;

    //create
    public AccountResponse createAccount(AccountRequest request) {
        Optional<ClientEntity> optional = clientRepository.findByClientId(request.getClientId());
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Client not exists!");
        }
        ClientEntity clientEntity = optional.get();
        AccountEntity accountEntity = accountMapper.fromDto(request);
        accountEntity.setClient(clientEntity);
        accountRepository.save(accountEntity);
        return accountMapper.toDto(accountEntity);
     }
    //read
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream().map(accountMapper::toDto).toList();
    }

    public AccountResponse getAccountById(Long id) {
        Optional<AccountEntity> optional = accountRepository.findById(id);
        if (optional.isPresent()) {
            return accountMapper.toDto(optional.get());
        }
        throw new EntityNotFoundException("Account not found!");
    }
    //update
    public AccountResponse updateAccountById(Long id, AccountRequest request) {
        Optional<ClientEntity> optional = clientRepository.findByClientId(request.getClientId());
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Client not exists!");
        }
        ClientEntity clientEntity = optional.get();
        Optional<AccountEntity> optionalAccount = accountRepository.findById(id);
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
    public AccountResponse deleteAccountById(Long id) {
        Optional<AccountEntity> optional = accountRepository.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Account not exists!");
        }
        AccountEntity accountEntity = optional.get();
        accountRepository.delete(accountEntity);
        return accountMapper.toDto(accountEntity);
    }
}
