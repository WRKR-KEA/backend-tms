package com.wrkr.tickety.domains.member.presentation.util.validator;

import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.member.presentation.util.annotation.PasswordFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordFormatValidator implements ConstraintValidator<PasswordFormat, String> {

    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,12}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    @Override
    public void initialize(PasswordFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        boolean isValid = PASSWORD_PATTERN.matcher(password).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MemberErrorCode.INVALID_PASSWORD_FORMAT.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
