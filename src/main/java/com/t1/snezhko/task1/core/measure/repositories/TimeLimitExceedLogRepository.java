package com.t1.snezhko.task1.core.measure.repositories;

import com.t1.snezhko.task1.core.measure.entities.TimeLimitExceedLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeLimitExceedLogRepository extends JpaRepository<TimeLimitExceedLogEntity, Long> {

}
