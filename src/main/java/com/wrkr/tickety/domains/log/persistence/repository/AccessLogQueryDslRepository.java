package com.wrkr.tickety.domains.log.persistence.repository;

import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.persistence.entity.AccessLogEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccessLogQueryDslRepository {

    Page<AccessLogEntity> searchAccessLogs(Pageable pageable, String query, ActionType action, LocalDate startDate, LocalDate endDate);

    List<AccessLogEntity> findAllAccessLogs(String query, ActionType action, LocalDate startDate, LocalDate endDate);
}
