package com.wrkr.tickety.domains.log.application.usecase;

import com.wrkr.tickety.domains.log.application.dto.response.AccessLogSearchResponse;
import com.wrkr.tickety.domains.log.application.mapper.LogMapper;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.domain.service.AccessLogGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessLogExcelDownloadUseCase {

    private final AccessLogGetService accessLogGetService;

    public List<AccessLogSearchResponse> getAllAccessLogs(String query, ActionType action, String startDateReq,
        String endDateReq) {
        LocalDate startDate = parseLocalDateOrNull(startDateReq);
        LocalDate endDate = parseLocalDateOrNull(endDateReq);

        validatePeriod(startDate, endDate);

        List<AccessLog> accessLogs = accessLogGetService.getAllAccessLogs(query, action, startDate, endDate);

        return accessLogs.stream()
            .map(LogMapper::toAccessLogSearchResponse)
            .toList();
    }

    private static void validatePeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw ApplicationException.from(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
    }

    private LocalDate parseLocalDateOrNull(String date) {
        try {
            return LocalDate.parse(date);
        } catch (NullPointerException | DateTimeException e) {
            return null;
        }
    }
}
