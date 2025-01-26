package com.wrkr.tickety.domains.member.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_401", "회원을 찾을 수 없습니다."),

    MEMBER_NOT_ALLOWED(HttpStatus.FORBIDDEN, "MEMBER_301", "접근 권한이 없는 회원입니다."),

    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER_001", "유효하지 않은 이메일 형식입니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER_002", "이미 사용중인 이메일입니다."),
    INVALID_PHONE_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER_003", "유효하지 않은 전화번호 형식입니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "MEMBER_004", "유효하지 않은 역할입니다."),
    ALREADY_EXIST_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER_005", "이미 사용중인 닉네임입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
