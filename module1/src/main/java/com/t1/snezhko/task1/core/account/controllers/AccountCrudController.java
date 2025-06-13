package com.t1.snezhko.task1.core.account.controllers;

import com.t1.snezhko.task1.core.account.dto.CreateAccountRequest;
import com.t1.snezhko.task1.core.account.dto.AccountResponse;
import com.t1.snezhko.task1.core.account.services.AccountCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/accounts")
public class AccountCrudController {
    @Autowired
    private AccountCrudService accountCrudService;

    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(OK).body(accountCrudService.createAccount(request));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.status(OK).body(accountCrudService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable("id") String id) {
        return ResponseEntity.status(OK).body(accountCrudService.getAccountById(UUID.fromString(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccountById(@PathVariable("id") String id, @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(OK).body(accountCrudService.updateAccountById(UUID.fromString(id), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AccountResponse> deleteAccountById(@PathVariable("id") String id) {
        return ResponseEntity.status(OK).body(accountCrudService.deleteAccountById(UUID.fromString(id)));
    }
}
