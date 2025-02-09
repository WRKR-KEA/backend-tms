package com.wrkr.tickety.domains.member.presentation.util.annotation;

import com.wrkr.tickety.domains.member.presentation.util.validator.EmailExistsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EmailExistsValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistEmail {

    String message() default "이 이메일은 이미 사용 중입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
