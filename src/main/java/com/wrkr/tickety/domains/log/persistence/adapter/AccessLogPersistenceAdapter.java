package com.wrkr.tickety.domains.log.persistence.adapter;


import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.persistence.entity.AccessLogEntity;
import com.wrkr.tickety.domains.log.persistence.mapper.AccessLogPersistenceMapper;
import com.wrkr.tickety.domains.log.persistence.repository.AccessLogRepository;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<AccessLog> searchAccessLogs(final Pageable pageable, final Role role, final String query, final ActionType action) {
        Page<AccessLogEntity> accessLogEntities = accessLogRepository.searchAccessLogs(pageable, role, query, action);
        return accessLogEntities.map(this.accessLogPersistenceMapper::toDomain);
    }
}
