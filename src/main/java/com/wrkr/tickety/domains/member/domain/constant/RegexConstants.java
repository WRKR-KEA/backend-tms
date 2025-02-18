package com.wrkr.tickety.domains.member.domain.constant;

public class RegexConstants {

    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%%+-]+@(gachon\\.ac\\.kr|gmail\\.com)$";
    public static final String NICKNAME_REGEX = "^[a-z]{3,10}\\.[a-z]{1,5}$";
    public static final String PHONE_NUMBER_REGEX = "^010-\\d{4}-\\d{4}$";
}
