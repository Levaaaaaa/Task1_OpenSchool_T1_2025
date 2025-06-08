package com.t1.snezhko.task1.core.make_transaction.arrest_account;

import com.t1.snezhko.task1.core.account.dto.AccountResponse;
import com.t1.snezhko.task1.core.account.dto.CreateAccountRequest;
import com.t1.snezhko.task1.core.client.dto.ClientDTO;
import com.t1.snezhko.task1.core.transaction.dto.CreateTransactionRequest;
import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import org.springframework.stereotype.Service;

@Service
public interface ArrestAccountService {
    public TransactionResponse checkAccountAndArrest(ClientDTO client, TransactionResponse response);
}
