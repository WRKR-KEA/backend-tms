package com.wrkr.tickety.domains.member.presentation.util;

import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<EmailFormat, String> {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@gachon\\.ac\\.kr$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public void initialize(EmailFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        boolean isValid = EMAIL_PATTERN.matcher(email).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MemberErrorCode.INVALID_EMAIL_FORMAT.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
