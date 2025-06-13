package com.t1.snezhko.accept_service.reject;

import com.t1.snezhko.core.transaction.dto.AcceptTransactionRequest;
import org.springframework.stereotype.Service;

@Service
public interface CheckRejectTransactionService {
    public boolean isRejected(AcceptTransactionRequest request);
}
