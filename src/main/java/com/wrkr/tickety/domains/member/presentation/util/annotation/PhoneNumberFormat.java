package com.wrkr.tickety.domains.member.presentation.util.annotation;

import com.wrkr.tickety.domains.member.presentation.util.validator.PhoneFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneFormatValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumberFormat {
    String message() default "전화번호 형식이 올바르지 않습니다."; // 기본 오류 메시지
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
