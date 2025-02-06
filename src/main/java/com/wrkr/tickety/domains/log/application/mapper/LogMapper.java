package com.wrkr.tickety.domains.log.application.mapper;

import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;

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
}
