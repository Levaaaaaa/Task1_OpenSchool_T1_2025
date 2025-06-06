package com.t1.snezhko.task1.core.transaction.services.crud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.persistence.entity.AccountEntity;
import com.t1.snezhko.task1.core.account.persistence.repositories.AccountRepository;
import com.t1.snezhko.task1.aop.annotations.Cached;
import com.t1.snezhko.task1.core.transaction.TransactionStatus;
import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.core.transaction.persistence.entity.TransactionEntity;
import com.t1.snezhko.task1.core.transaction.persistence.mappers.TransactionMapper;
import com.t1.snezhko.task1.core.transaction.persistence.repositories.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
class TransactionCrudServiceImpl implements TransactionCrudService{
    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    //create
    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        TransactionEntity transactionEntity = transactionMapper.fromDto(request);
        Optional<AccountEntity> optionalConsumer = accountRepository.findByAccountId(request.getConsumer());
        if (optionalConsumer.isEmpty()) {
            throw new EntityNotFoundException("Consumer not found!");
        }
        Optional<AccountEntity> optionalProducer = accountRepository.findByAccountId(request.getProducer());
        if (optionalProducer.isEmpty()) {
            throw new EntityNotFoundException("Producer not found!");
        }

        AccountEntity producer = optionalProducer.get();
        AccountEntity consumer = optionalConsumer.get();

        if (producer.getAmount().compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient funds!");
        }

        if (!producer.getStatus().equals(AccountStatus.OPEN)) {
            throw new IllegalStateException("Producer's account is unavailable");
        }

        if (!consumer.getStatus().equals(AccountStatus.OPEN)) {
            throw new IllegalStateException("Consumer's account is unavailable");
        }

        producer.setAmount(producer.getAmount().subtract(request.getAmount()));
//        consumer.setAmount(consumer.getAmount().add(request.getAmount()));

        transactionEntity.setProducer(optionalProducer.get());
        transactionEntity.setConsumer(optionalConsumer.get());

        accountRepository.save(producer);
        //      accountRepository.save(consumer);

        transactionEntity.setTransactionDate(LocalDateTime.now());
        transactionEntity.setStatus(TransactionStatus.REQUESTED);
        transactionEntity.setTransactionId(UUID.randomUUID());
        transactionRepository.save(transactionEntity);

        return transactionMapper.toDto(transactionEntity);
    }


    //read
    @Cached
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream().map(transactionMapper::toDto).toList();
    }

    @Cached
    public TransactionResponse getTransactionById(UUID id) {
        TransactionEntity entity = getTransactionEntity(id);
        return transactionMapper.toDto(entity);
    }

    //delete
    @Transactional
    public TransactionResponse deleteTransaction(UUID id) {
        TransactionEntity transaction = getTransactionEntity(id);
        AccountEntity producer = accountRepository.findById(transaction.getProducer().getId()).get();
        AccountEntity consumer = accountRepository.findById(transaction.getConsumer().getId()).get();

        producer.setAmount(producer.getAmount().add(transaction.getAmount()));
        consumer.setAmount(consumer.getAmount().subtract(transaction.getAmount()));
        accountRepository.save(producer);
        accountRepository.save(consumer);
        transactionRepository.delete(transaction);
        return transactionMapper.toDto(transaction);
    }

    @Override
    public TransactionResponse updateTransactionStatus(UUID id, TransactionStatus status) {
        TransactionEntity entity = getTransactionEntity(id);
        entity.setStatus(status);
        transactionRepository.save(entity);
        return transactionMapper.toDto(entity);
    }

    private TransactionEntity getTransactionEntity(UUID id) {
        Optional<TransactionEntity> optional = transactionRepository.findByTransactionId(id);
        if (optional.isEmpty())
        {
            throw new EntityNotFoundException("Transaction " + id + " not found!");
        }
        return optional.get();
    }
}
