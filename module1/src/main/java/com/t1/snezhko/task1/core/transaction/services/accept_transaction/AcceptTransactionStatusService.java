package com.t1.snezhko.task1.core.transaction.services.accept_transaction;

import com.t1.snezhko.task1.core.transaction.dto.TransactionResponse;
import com.t1.snezhko.task1.core.transaction.dto.check_status.CheckTransactionStatusResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface AcceptTransactionStatusService {
    @Transactional
    public TransactionResponse acceptStatus(CheckTransactionStatusResponse response);
}
