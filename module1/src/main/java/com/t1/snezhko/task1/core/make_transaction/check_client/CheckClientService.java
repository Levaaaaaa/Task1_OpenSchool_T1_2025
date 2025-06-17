package com.t1.snezhko.task1.core.make_transaction.check_client;

import com.t1.snezhko.task1.core.make_transaction.dto.CheckClientRequest;
import com.t1.snezhko.task1.core.make_transaction.dto.CheckClientResponse;
import org.springframework.stereotype.Service;

@Service
public interface CheckClientService {
    public CheckClientResponse checkClient(CheckClientRequest request);
}
