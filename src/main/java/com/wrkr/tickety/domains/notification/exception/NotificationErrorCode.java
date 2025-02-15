package com.wrkr.tickety.domains.notification.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements BaseErrorCode {

    RECEIVER_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_401", "수신자를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
