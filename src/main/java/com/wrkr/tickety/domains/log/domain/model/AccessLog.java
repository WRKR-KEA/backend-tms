package com.wrkr.tickety.domains.log.domain.model;

import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.global.model.BaseTime;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessLog extends BaseTime {

    private Long accessLogId;
    private String nickname;
    private String ip;
    private ActionType action;
    private LocalDateTime accessAt;
    private Boolean isSuccess;

    @Builder
    public AccessLog(Long accessLogId, String nickname, String ip, ActionType action, LocalDateTime accessAt, Boolean isSuccess) {
        this.accessLogId = accessLogId;
        this.nickname = nickname;
        this.ip = ip;
        this.action = action;
        this.accessAt = accessAt;
        this.isSuccess = isSuccess;
    }
}
