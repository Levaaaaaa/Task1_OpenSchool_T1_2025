package com.t1.snezhko.task1.client.persistence.repositories;

import com.t1.snezhko.task1.client.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    public Optional<ClientEntity> findByClientId(UUID clientId);
}
