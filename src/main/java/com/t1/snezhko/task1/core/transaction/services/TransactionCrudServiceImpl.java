package com.t1.snezhko.task1.core.transaction.services;

import com.t1.snezhko.task1.core.account.persistence.entity.AccountEntity;
import com.t1.snezhko.task1.core.account.persistence.repositories.AccountRepository;
import com.t1.snezhko.task1.aop.annotations.Cached;
import com.t1.snezhko.task1.core.transaction.dto.TransactionRequest;
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
    public TransactionResponse createTransaction(TransactionRequest request) {
        TransactionEntity transactionEntity = transactionMapper.fromDto(request);
        Optional<AccountEntity> optionalConsumer = accountRepository.findById(request.getConsumer());
        if (optionalConsumer.isEmpty()) {
            throw new EntityNotFoundException("Consumer not found!");
        }
        Optional<AccountEntity> optionalProducer = accountRepository.findById(request.getProducer());
        if (optionalProducer.isEmpty()) {
            throw new EntityNotFoundException("Producer not found!");
        }

        AccountEntity producer = optionalProducer.get();
        AccountEntity consumer = optionalConsumer.get();

        if (producer.getAmount().compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient funds!");
        }

        producer.setAmount(producer.getAmount().subtract(request.getAmount()));
        consumer.setAmount(consumer.getAmount().add(request.getAmount()));

        transactionEntity.setProducer(optionalProducer.get());
        transactionEntity.setConsumer(optionalConsumer.get());

        accountRepository.save(producer);
        accountRepository.save(consumer);

        transactionEntity.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transactionEntity);

        return transactionMapper.toDto(transactionEntity);
    }

    //read
    @Cached
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream().map(transactionMapper::toDto).toList();
    }

    @Cached
    public TransactionResponse getTransactionById(Long id) {
        Optional<TransactionEntity> optional = transactionRepository.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Transaction not found!");
        }
        return transactionMapper.toDto(optional.get());
    }

    //delete
    @Transactional
    public TransactionResponse deleteTransaction(Long id) {
        Optional<TransactionEntity> optional = transactionRepository.findById(id);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Transaction not found!");
        }
        TransactionEntity transaction = optional.get();
        AccountEntity producer = accountRepository.findById(transaction.getProducer().getId()).get();
        AccountEntity consumer = accountRepository.findById(transaction.getConsumer().getId()).get();

        producer.setAmount(producer.getAmount().add(transaction.getAmount()));
        consumer.setAmount(consumer.getAmount().subtract(transaction.getAmount()));
        accountRepository.save(producer);
        accountRepository.save(consumer);
        transactionRepository.delete(transaction);
        return transactionMapper.toDto(transaction);
    }
}
