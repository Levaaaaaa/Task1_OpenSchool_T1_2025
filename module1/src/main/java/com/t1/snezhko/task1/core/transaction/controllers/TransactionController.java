package com.t1.snezhko.task1.core.transaction.controllers;

import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.core.transaction.services.crud.TransactionCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionCrudService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody CreateTransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @GetMapping
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable("id") String id) {
        return ResponseEntity.ok(transactionService.getTransactionById(UUID.fromString(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TransactionResponse> deleteTransaction(@PathVariable("id") String id) {
        return ResponseEntity.ok(transactionService.deleteTransaction(UUID.fromString(id)));
    }
}
