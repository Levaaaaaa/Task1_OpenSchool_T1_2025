package com.t1.snezhko.task1.core.errorlog.repository;

import com.t1.snezhko.task1.core.errorlog.entity.DataSourceErrorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSourceErrorLogRepository extends JpaRepository<DataSourceErrorLogEntity, Long> {

}
