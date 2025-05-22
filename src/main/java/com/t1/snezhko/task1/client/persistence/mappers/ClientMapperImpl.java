package com.t1.snezhko.task1.client.persistence.mappers;

import com.t1.snezhko.task1.client.dto.ClientDTO;
import com.t1.snezhko.task1.client.persistence.entity.ClientEntity;
import org.springframework.stereotype.Service;

@Service
class ClientMapperImpl implements ClientMapper{
    @Override
    public ClientDTO toDto(ClientEntity entity) {
        return ClientDTO.builder()
                .clientId(entity.getClientId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .patronymic(entity.getPatronymic())
                .build();
    }

    @Override
    public ClientEntity fromDto(ClientDTO dto) {
        return ClientEntity.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .patronymic(dto.getPatronymic())
                .clientId(dto.getClientId())
                .build();
    }
}
