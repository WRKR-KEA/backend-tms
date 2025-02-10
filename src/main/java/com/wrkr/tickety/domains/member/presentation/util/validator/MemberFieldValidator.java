package com.wrkr.tickety.domains.member.presentation.util.validator;

import static com.wrkr.tickety.domains.member.domain.constant.RegexConstants.EMAIL_REGEX;
import static com.wrkr.tickety.domains.member.domain.constant.RegexConstants.NICKNAME_REGEX;
import static com.wrkr.tickety.domains.member.domain.constant.RegexConstants.PHONE_NUMBER_REGEX;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_NICKNAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.DEPARTMENT_IS_BLANK;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.EMAIL_IS_BLANK;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_EMAIL_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_NICKNAME_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_PHONE_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_ROLE;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.NAME_IS_BLANK;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.NICKNAME_IS_BLANK;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.PHONE_IS_BLANK;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.POSITION_IS_BLANK;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberFieldValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEX);
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    private final MemberGetService memberGetService;

    public void validateField(
        String name,
        String department,
        String position,
        String phone,
        Role role,
        String nickname,
        String email
    ) {
        validateName(name);
        validateDepartment(department);
        validatePosition(position);
        validatePhone(phone);
        validateRole(role);
        validateNickname(nickname);
        validateEmail(email);
    }

    public void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw ApplicationException.from(EMAIL_IS_BLANK);
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw ApplicationException.from(INVALID_EMAIL_FORMAT);
        }

        if (memberGetService.existsByEmail(email)) {
            throw ApplicationException.from(ALREADY_EXIST_EMAIL);
        }
    }

    public void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw ApplicationException.from(NAME_IS_BLANK);
        }
    }

    public void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw ApplicationException.from(NICKNAME_IS_BLANK);
        }

        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw ApplicationException.from(INVALID_NICKNAME_FORMAT);
        }

        if (memberGetService.existsByNickname(nickname)) {
            throw ApplicationException.from(ALREADY_EXIST_NICKNAME);
        }
    }

    public void validateDepartment(String department) {
        if (department == null || department.isBlank()) {
            throw ApplicationException.from(DEPARTMENT_IS_BLANK);
        }
    }

    public void validatePosition(String position) {
        if (position == null || position.isBlank()) {
            throw ApplicationException.from(POSITION_IS_BLANK);
        }
    }

    public void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw ApplicationException.from(PHONE_IS_BLANK);
        }

        if (!PHONE_NUMBER_PATTERN.matcher(phone).matches()) {
            throw ApplicationException.from(INVALID_PHONE_FORMAT);
        }
    }

    public void validateRole(Role role) {
        if (role == null) {
            throw ApplicationException.from(INVALID_ROLE);
        }
    }
}
