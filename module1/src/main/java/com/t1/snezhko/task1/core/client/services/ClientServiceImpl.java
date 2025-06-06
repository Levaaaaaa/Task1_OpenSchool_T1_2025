package com.t1.snezhko.task1.core.client.services;

import com.t1.snezhko.task1.core.client.ClientStatus;
import com.t1.snezhko.task1.core.client.dto.ClientDTO;
import com.t1.snezhko.task1.core.client.persistence.entity.ClientEntity;
import com.t1.snezhko.task1.core.client.persistence.mappers.ClientMapper;
import com.t1.snezhko.task1.core.client.persistence.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
class ClientServiceImpl implements ClientService{
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Override
    public ClientDTO updateClientStatus(UUID clientId, ClientStatus newStatus) {
        ClientEntity entity = getClientEntity(clientId);
        entity.setStatus(newStatus);
        clientRepository.save(entity);
        return clientMapper.toDto(entity);
    }

    @Override
    public ClientDTO getClientById(UUID id) {
        ClientEntity entity = getClientEntity(id);
        return clientMapper.toDto(entity);
    }

    private ClientEntity getClientEntity(UUID clientId) {
        Optional<ClientEntity> optional = clientRepository.findByClientId(clientId);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Client with id " + clientId + " not found!");
        }
        return optional.get();
    }
}
