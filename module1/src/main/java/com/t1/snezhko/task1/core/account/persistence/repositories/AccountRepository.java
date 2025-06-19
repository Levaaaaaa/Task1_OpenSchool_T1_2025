package com.t1.snezhko.task1.core.account.persistence.repositories;

import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.persistence.entity.AccountEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    public Optional<AccountEntity> findByAccountId(UUID accountId);
    public List<AccountEntity> findByStatus(AccountStatus status, Pageable pageable);
}

//2025-06-11 20:17:45 Field accountRepository in com.t1.snezhko.task1.core.account.services.AccountCrudServiceImpl required a bean of type 'com.t1.snezhko.task1.core.account.persistence.repositories.AccountRepository' that could not be found.