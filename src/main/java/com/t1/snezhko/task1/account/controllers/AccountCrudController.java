package com.t1.snezhko.task1.account.controllers;

import com.t1.snezhko.task1.account.dto.AccountRequest;
import com.t1.snezhko.task1.account.dto.AccountResponse;
import com.t1.snezhko.task1.account.services.AccountCrudService;
import com.t1.snezhko.task1.aop.annotations.LogException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/accounts")
@LogException
public class AccountCrudController {
    @Autowired
    private AccountCrudService accountCrudService;

    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody AccountRequest request) {
        return ResponseEntity.status(OK).body(accountCrudService.createAccount(request));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.status(OK).body(accountCrudService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable("id") Long id) {
        return ResponseEntity.status(OK).body(accountCrudService.getAccountById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccountById(@PathVariable("id") Long id, @RequestBody AccountRequest request) {
        return ResponseEntity.status(OK).body(accountCrudService.updateAccountById(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AccountResponse> deleteAccountById(@PathVariable("id") Long id) {
        return ResponseEntity.status(OK).body(accountCrudService.deleteAccountById(id));
    }
}
