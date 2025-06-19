package com.t1.snezhko.task1.core.client.services;

import com.t1.snezhko.task1.core.client.ClientStatus;
import com.t1.snezhko.task1.core.client.dto.ClientDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ClientService {
    public ClientDTO updateClientStatus(UUID clientId, ClientStatus newStatus);
    public ClientDTO getClientById(UUID id);
}
