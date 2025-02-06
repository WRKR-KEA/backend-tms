package com.wrkr.tickety.domains.log.application.usecase;

import com.wrkr.tickety.domains.log.application.dto.response.AccessLogSearchResponse;
import com.wrkr.tickety.domains.log.application.mapper.LogMapper;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.domain.service.AccessLogGetService;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessLogSearchUseCase {

    private final AccessLogGetService accessLogGetService;

    public ApplicationPageResponse<AccessLogSearchResponse> searchAccessLogs(Pageable pageable, Role role, String query, ActionType action) {
        Page<AccessLog> accessLogsPage = accessLogGetService.searchAccessLogs(pageable, role, query, action);
        return ApplicationPageResponse.of(accessLogsPage, LogMapper::toAccessLogSearchResponse);
    }
}
