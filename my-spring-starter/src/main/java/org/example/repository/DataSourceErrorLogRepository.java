package org.example.repository;

import org.example.entity.DataSourceErrorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSourceErrorLogRepository extends JpaRepository<DataSourceErrorLogEntity, Long> {

}
