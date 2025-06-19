package org.example.repository;

import org.example.entity.TimeLimitExceedLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeLimitExceedLogRepository extends JpaRepository<TimeLimitExceedLogEntity, Long> {

}
