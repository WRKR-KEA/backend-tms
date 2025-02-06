package com.wrkr.tickety.domains.log.domain.service;

import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.persistence.adapter.AccessLogPersistenceAdapter;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccessLogGetService {

    private final AccessLogPersistenceAdapter accessLogPersistenceAdapter;

    public Page<AccessLog> searchAccessLogs(Pageable pageable, Role role, String query, ActionType action) {
        return accessLogPersistenceAdapter.searchAccessLogs(pageable, role, query, action);
    }
}
