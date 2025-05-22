package com.t1.snezhko.task1.account.persistence.repositories;

import com.t1.snezhko.task1.account.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

}
