package com.wrkr.tickety.infrastructure.email;

public class EmailConstants {

    public static final String FILENAME_PASSWORD = "password";
    public static final String FILENAME_VERIFICATION_CODE = "verification-code";
    public static final String TEMP_PASSWORD_SUBJECT = "[Tickety] 임시 비밀번호 발급";
    public static final String REISSUE_PASSWORD_SUBJECT = "[Tickety] 비밀번호 재발급";
    public static final String VERIFICATION_CODE_SUBJECT = "[Tickety] 인증번호 발급";
    public static final String TICKET_STATUS_CHANGE = "ticketStatusChange";
    public static final String TICKET_STATUS_CHANGE_SUBJECT = "[Tickety] 티켓 상태 변경";
    public static final String TICKET_DELEGATE_TO_USER = "delegateEmailToUser";
    public static final String TICKET_DELEGATE_TO_NEW_MANAGER = "delegateEmailToNewManager";
    public static final String TICKET_DELEGATE_SUBJECT = "[Tickety] 티켓 담당자 변경";
    public static final String TICKET_COMMENT = "commentCreate";
    public static final String TICKET_COMMENT_SUBJECT = "[Tickety] 티켓 코멘트 작성";
    public static final String REMIND_SUBJECT = "[Tickety] 티켓 리마인드 알림";
    public static final String REMIND_TYPE = "pushRemind";
}
