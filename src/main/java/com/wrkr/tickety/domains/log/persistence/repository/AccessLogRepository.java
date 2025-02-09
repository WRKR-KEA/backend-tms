package com.wrkr.tickety.domains.log.persistence.repository;

import com.wrkr.tickety.domains.log.persistence.entity.AccessLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLogEntity, Long>, AccessLogQueryDslRepository {

}
