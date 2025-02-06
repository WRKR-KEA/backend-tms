package com.wrkr.tickety.domains.log.persistence.repository;

import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.persistence.entity.AccessLogEntity;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccessLogQueryDslRepository {

    Page<AccessLogEntity> searchAccessLogs(Pageable pageable, Role role, String query, ActionType action);
}
