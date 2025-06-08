package com.t1.snezhko.accept_service.reject;

import com.t1.snezhko.core.transaction.dto.AcceptTransactionRequest;
import org.springframework.stereotype.Service;

@Service
class CheckRejectTransactionServiceImpl implements CheckRejectTransactionService{

    @Override
    public boolean isRejected(AcceptTransactionRequest request) {
        return request.getTransactionAmount().compareTo(request.getAccountBalance()) > 0;
    }
}
