package com.wrkr.tickety.domains.log.application.mapper;

import com.wrkr.tickety.domains.log.application.dto.response.AccessLogSearchResponse;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.global.utils.PkCrypto;

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
            .accessLogId(PkCrypto.encrypt(accessLog.getAccessLogId()))
            .nickname(accessLog.getNickname())
            .role(accessLog.getRole())
            .ip(accessLog.getIp())
            .action(accessLog.getAction())
            .accessAt(accessLog.getAccessAt())
            .isSuccess(accessLog.getIsSuccess())
            .build();
    }
}
