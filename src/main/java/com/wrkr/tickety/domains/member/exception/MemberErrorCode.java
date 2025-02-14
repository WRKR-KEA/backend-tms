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

    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER_001", "유효하지 않은 이메일입니다."),
    INVALID_PHONE(HttpStatus.BAD_REQUEST, "MEMBER_002", "유효하지 않은 전화번호입니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "MEMBER_003", "유효하지 않은 권한입니다."),
    DELETED_MEMBER(HttpStatus.BAD_REQUEST, "MEMBER_004", "삭제된 회원입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER_005", "유효하지 않은 비밀번호 형식입니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER_006", "유효하지 않은 아이디입니다."),
    UNMATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER_007", "비밀번호가 일치하지 않습니다."),

    INVALID_NAME(HttpStatus.BAD_REQUEST, "MEMBER_008", "유효하지 않은 이름입니다."),
    INVALID_DEPARTMENT(HttpStatus.BAD_REQUEST, "MEMBER_009", "유효하지 않은 부서입니다."),
    INVALID_POSITION(HttpStatus.BAD_REQUEST, "MEMBER_010", "유효하지 않은 직책입니다."),

    ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT, "MEMBER_901", "이미 사용중인 이메일입니다."),
    ALREADY_EXIST_NICKNAME(HttpStatus.CONFLICT, "MEMBER_902", "이미 사용중인 아이디입니다."),
    ;


    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
