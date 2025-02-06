package com.wrkr.tickety.domains.log.domain.service;

import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.persistence.adapter.AccessLogPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccessLogSaveService {

    private final AccessLogPersistenceAdapter accessLogPersistenceAdapter;

    public AccessLog save(AccessLog accessLog) {
        return accessLogPersistenceAdapter.save(accessLog);
    }
}
