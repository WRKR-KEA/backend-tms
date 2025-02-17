package com.wrkr.tickety.domains.member.presentation.util.validator;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.EXCEED_NICKNAME_MAX_LENGTH;

import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.member.presentation.util.annotation.NicknameFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class NicknameFormatValidator implements ConstraintValidator<NicknameFormat, String> {

    private static final String NICKNAME_REGEX = "^[a-z]{3,10}\\.[a-z]{1,5}$";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEX);

    @Override
    public void initialize(NicknameFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        if (nickname.length() > 50) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(EXCEED_NICKNAME_MAX_LENGTH.getMessage()).addConstraintViolation();
            return false;
        }

        boolean isValid = nickname != null && !nickname.isBlank() && NICKNAME_PATTERN.matcher(nickname).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MemberErrorCode.INVALID_NICKNAME.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
