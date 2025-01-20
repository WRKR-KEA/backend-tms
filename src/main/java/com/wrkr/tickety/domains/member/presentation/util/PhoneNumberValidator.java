package com.wrkr.tickety.domains.member.presentation.util;

import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberFormat, String> {
    private static final String PHONE_NUMBER_REGEX = "^010-\\d{4}-\\d{4}$";
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    @Override
    public void initialize(PhoneNumberFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        boolean isValid = PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MemberErrorCode.INVALID_PHONE_FORMAT.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
