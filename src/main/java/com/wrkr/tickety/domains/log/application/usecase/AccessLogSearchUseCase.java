package com.wrkr.tickety.domains.log.application.usecase;

import com.wrkr.tickety.domains.log.application.dto.response.AccessLogSearchResponse;
import com.wrkr.tickety.domains.log.application.mapper.LogMapper;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.domain.service.AccessLogGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import java.time.DateTimeException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessLogSearchUseCase {

    private final AccessLogGetService accessLogGetService;

    public ApplicationPageResponse<AccessLogSearchResponse> searchAccessLogs(Pageable pageable, String query, ActionType action, String startDateReq,
        String endDateReq) {
        LocalDate startDate = parseLocalDateOrNull(startDateReq);
        LocalDate endDate = parseLocalDateOrNull(endDateReq);

        validatePeriod(startDate, endDate);

        Page<AccessLog> accessLogsPage = accessLogGetService.searchAccessLogs(pageable, query, action, startDate, endDate);
        return ApplicationPageResponse.of(accessLogsPage, LogMapper::toAccessLogSearchResponse);
    }

    private LocalDate parseLocalDateOrNull(String date) {
        try {
            return LocalDate.parse(date);
        } catch (NullPointerException | DateTimeException e) {
            return null;
        }
    }

    private static void validatePeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw ApplicationException.from(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
    }
}
