package com.t1.snezhko.accept_service.block;

import com.t1.snezhko.core.transaction.dto.AcceptTransactionRequest;
import org.springframework.stereotype.Service;

@Service
public interface CheckBlockedTransactionService {
    public boolean isBlocked(AcceptTransactionRequest request);
}
