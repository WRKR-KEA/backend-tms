package com.wrkr.tickety.domains.member.presentation.util.validator;

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
        boolean isValid = NICKNAME_PATTERN.matcher(nickname).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MemberErrorCode.INVALID_NICKNAME_FORMAT.getMessage()).addConstraintViolation();
        }

        return true;
    }
}
