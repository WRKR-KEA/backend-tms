package com.wrkr.tickety.domains.log.domain.service;

import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.persistence.adapter.AccessLogPersistenceAdapter;
import java.time.LocalDate;
import java.util.List;
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

    public Page<AccessLog> searchAccessLogs(Pageable pageable, String query, ActionType action, LocalDate startDate, LocalDate endDate) {
        return accessLogPersistenceAdapter.searchAccessLogs(pageable, query, action, startDate, endDate);
    }

    public List<AccessLog> getAllAccessLogs(String query, ActionType action, LocalDate startDate, LocalDate endDate) {
        return accessLogPersistenceAdapter.findAllAccessLogs(query, action, startDate, endDate);
    }
}
