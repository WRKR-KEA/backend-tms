package com.wrkr.tickety.domains.log.persistence.adapter;

import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.persistence.entity.AccessLogEntity;
import com.wrkr.tickety.domains.log.persistence.mapper.AccessLogPersistenceMapper;
import com.wrkr.tickety.domains.log.persistence.repository.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccessLogPersistenceAdapter {

    private final AccessLogRepository accessLogRepository;
    private final AccessLogPersistenceMapper accessLogPersistenceMapper;

    public AccessLog save(final AccessLog accessLog) {
        AccessLogEntity accessLogEntity = this.accessLogPersistenceMapper.toEntity(accessLog);
        AccessLogEntity savedEntity = this.accessLogRepository.save(accessLogEntity);
        return this.accessLogPersistenceMapper.toDomain(savedEntity);
    }
}
