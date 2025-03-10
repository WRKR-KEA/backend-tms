package com.wrkr.tickety.domains.member.presentation.util.validator;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.member.presentation.util.annotation.RoleFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleFormatValidator implements ConstraintValidator<RoleFormat, Role> {

    @Override
    public void initialize(RoleFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext context) {
        boolean isValid = false;

        for (Role r : Role.values()) {
            if (r.equals(role)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MemberErrorCode.INVALID_ROLE.getMessage())
                .addConstraintViolation();
        }

        return isValid;
    }
}