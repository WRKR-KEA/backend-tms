package com.wrkr.tickety.global.annotation.swagger;

import com.wrkr.tickety.domains.auth.exception.AuthErrorCode;
import com.wrkr.tickety.domains.ticket.exception.*;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import com.wrkr.tickety.domains.ticket.exception.CommentErrorCode;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.exception.StatisticsErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomErrorCodes {

    CommonErrorCode[] commonErrorCodes() default {};

    MemberErrorCode[] memberErrorCodes() default {};
  
    TicketErrorCode[] ticketErrorCodes() default {};

    CommentErrorCode[] commentErrorCodes() default {};

    CategoryErrorCode[] categoryErrorCodes() default {};

    TemplateErrorCode[] templateErrorCodes() default {};

    GuideErrorCode[] value() default {};

    StatisticsErrorCode[] statisticsErrorCodes() default {};

    AuthErrorCode[] authErrorCodes() default {};

}
