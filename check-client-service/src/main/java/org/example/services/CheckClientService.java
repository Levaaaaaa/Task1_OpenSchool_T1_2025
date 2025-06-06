package org.example.services;

import org.example.dto.CheckClientRequest;
import org.example.dto.CheckClientResponse;
import org.springframework.stereotype.Service;

@Service
public interface CheckClientService {
    public CheckClientResponse checkClient(CheckClientRequest request);
}
