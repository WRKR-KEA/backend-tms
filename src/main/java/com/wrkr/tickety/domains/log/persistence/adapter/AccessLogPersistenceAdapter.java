package com.wrkr.tickety.domains.log.persistence.adapter;


import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.persistence.entity.AccessLogEntity;
import com.wrkr.tickety.domains.log.persistence.mapper.AccessLogPersistenceMapper;
import com.wrkr.tickety.domains.log.persistence.repository.AccessLogRepository;
import java.time.LocalDate;
import java.util.List;
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

    public Page<AccessLog> searchAccessLogs(final Pageable pageable, final String query, final ActionType action, final LocalDate startDate,
        final LocalDate endDate) {
        Page<AccessLogEntity> accessLogEntities = accessLogRepository.searchAccessLogs(pageable, query, action, startDate, endDate);
        return accessLogEntities.map(this.accessLogPersistenceMapper::toDomain);
    }

    public List<AccessLog> findAllAccessLogs(final String query, final ActionType action, final LocalDate startDate,
        final LocalDate endDate) {
        List<AccessLogEntity> accessLogEntities = accessLogRepository.findAllAccessLogs(query, action, startDate, endDate);
        return accessLogEntities.stream()
            .map(this.accessLogPersistenceMapper::toDomain).toList();
    }
}
