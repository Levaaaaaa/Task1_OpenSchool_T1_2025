package com.t1.snezhko.task1.transaction.controllers;

import com.t1.snezhko.task1.aop.annotations.LogException;
import com.t1.snezhko.task1.aop.annotations.Metric;
import com.t1.snezhko.task1.transaction.dto.TransactionRequest;
import com.t1.snezhko.task1.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.transaction.services.TransactionCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@LogException
public class TransactionController {
    @Autowired
    private TransactionCrudService transactionService;

    @PostMapping
    @Metric
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @GetMapping
    @Metric
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    @Metric
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @DeleteMapping("/{id}")
    @Metric
    public ResponseEntity<TransactionResponse> deleteTransaction(@PathVariable("id") Long id) {
        return ResponseEntity.ok(transactionService.deleteTransaction(id));
    }
}
