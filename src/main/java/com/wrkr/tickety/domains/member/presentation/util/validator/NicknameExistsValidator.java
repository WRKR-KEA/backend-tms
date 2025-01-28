package com.wrkr.tickety.domains.member.presentation.util.validator;

import com.wrkr.tickety.domains.member.domain.service.MemberGetService; // MemberGetService 임포트
import com.wrkr.tickety.domains.member.exception.MemberErrorCode; // MemberErrorCode 임포트
import com.wrkr.tickety.domains.member.presentation.util.annotation.ExistNickname; // ExistNickname 애노테이션 임포트
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NicknameExistsValidator implements ConstraintValidator<ExistNickname, String> {
    private final MemberGetService memberGetService;

    @Override
    public void initialize(ExistNickname constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        boolean isValid = !memberGetService.existsByNickname(nickname);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MemberErrorCode.ALREADY_EXIST_NICKNAME.getMessage())
                   .addConstraintViolation();
        }

        return isValid;
    }
}
