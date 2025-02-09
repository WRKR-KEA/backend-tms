package com.wrkr.tickety.global.response.code;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus getHttpStatus();
    String getCustomCode();
    String getMessage();
}
