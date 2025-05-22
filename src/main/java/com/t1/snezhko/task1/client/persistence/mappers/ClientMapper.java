package com.t1.snezhko.task1.client.persistence.mappers;

import com.t1.snezhko.task1.client.dto.ClientDTO;
import com.t1.snezhko.task1.client.persistence.entity.ClientEntity;
import org.springframework.stereotype.Service;

@Service
public interface ClientMapper {
    public ClientDTO toDto(ClientEntity entity);
    public ClientEntity fromDto(ClientDTO dto);
}
