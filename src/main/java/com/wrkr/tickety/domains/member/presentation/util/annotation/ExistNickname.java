package com.wrkr.tickety.domains.member.presentation.util.annotation;

import com.wrkr.tickety.domains.member.presentation.util.validator.NicknameExistsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NicknameExistsValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE }) // 적용할 대상
@Retention(RetentionPolicy.RUNTIME) // 런타임에 유지
public @interface ExistNickname {
    String message() default "이 닉네임은 이미 사용 중입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
