package com.t1.snezhko.task1.transaction.persistence.repositories;

import com.t1.snezhko.task1.aop.annotations.Cached;
import com.t1.snezhko.task1.transaction.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
}
