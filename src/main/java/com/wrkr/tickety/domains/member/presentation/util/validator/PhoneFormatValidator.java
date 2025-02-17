package com.wrkr.tickety.domains.member.presentation.util.validator;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.EXCEED_PHONE_MAX_LENGTH;

import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.member.presentation.util.annotation.PhoneNumberFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneFormatValidator implements ConstraintValidator<PhoneNumberFormat, String> {

    private static final String PHONE_NUMBER_REGEX = "^010-\\d{4}-\\d{4}$";
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    @Override
    public void initialize(PhoneNumberFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber.length() > 50) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(EXCEED_PHONE_MAX_LENGTH.getMessage()).addConstraintViolation();
            return false;
        }

        boolean isValid = phoneNumber != null && !phoneNumber.isBlank() && PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MemberErrorCode.INVALID_PHONE.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
