package com.t1.snezhko.task1.core.transaction.dto.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.snezhko.task1.core.transaction.dto.check_status.CheckTransactionStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AcceptTransactionRequestSerializer {
    @Autowired
    private ObjectMapper mapper;

    public String serialize(CheckTransactionStatusRequest request) throws JsonProcessingException {
        return mapper.writeValueAsString(request);
    }
}
