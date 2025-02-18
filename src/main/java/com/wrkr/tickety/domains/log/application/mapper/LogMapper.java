package com.wrkr.tickety.domains.log.application.mapper;

import com.wrkr.tickety.domains.log.application.dto.response.AccessLogExcelResponse;
import com.wrkr.tickety.domains.log.application.dto.response.AccessLogSearchResponse;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class LogMapper {

    private LogMapper() {
        throw new IllegalArgumentException();
    }

    public static AccessLog toAccessLog(String nickname, String ip, ActionType action, Boolean isSuccess) {
        return AccessLog.builder()
            .nickname(nickname)
            .ip(ip)
            .action(action)
            .isSuccess(isSuccess)
            .build();
    }

    public static AccessLogSearchResponse toAccessLogSearchResponse(AccessLog accessLog) {
        return AccessLogSearchResponse.builder()
            .accessLogId(String.valueOf(accessLog.getAccessLogId()))
            .nickname(accessLog.getNickname())
            .ip(accessLog.getIp())
            .action(accessLog.getAction())
            .accessAt(accessLog.getAccessAt())
            .isSuccess(accessLog.getIsSuccess())
            .build();
    }

    public static AccessLogExcelResponse mapToAccessLogExcelResponse(AccessLog accessLog) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.KOREA);

        return AccessLogExcelResponse.builder()
            .accessLogId(String.valueOf(accessLog.getAccessLogId()))
            .nickname(accessLog.getNickname())
            .ip(accessLog.getIp())
            .accessAt(accessLog.getAccessAt().format(formatter))
            .action(accessLog.getAction())
            .isSuccess(accessLog.getIsSuccess() ? "성공" : "실패")
            .build();
    }
}
