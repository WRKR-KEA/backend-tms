package com.wrkr.tickety.domains.member.presentation.util.validator;

import static com.wrkr.tickety.domains.member.domain.constant.RegexConstants.EMAIL_REGEX;
import static com.wrkr.tickety.domains.member.domain.constant.RegexConstants.NICKNAME_REGEX;
import static com.wrkr.tickety.domains.member.domain.constant.RegexConstants.PHONE_NUMBER_REGEX;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_NICKNAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_DEPARTMENT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_NAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_NICKNAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_PHONE;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_POSITION;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_ROLE;

import com.wrkr.tickety.domains.auth.exception.AuthErrorCode;
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
        validatePhoneFormat(phone);
        validateRole(role);
        validateNicknameFormat(nickname);
        validateNicknameDuplicate(nickname);
        validateEmailFormat(email);
        validateEmailDuplicate(email);
    }

    public void validateEmailFormat(String email) {
        if (email == null || email.isBlank()
            || !EMAIL_PATTERN.matcher(email).matches()) {
            throw ApplicationException.from(INVALID_EMAIL);
        }
    }

    public void validateEmailDuplicate(String email) {
        if (memberGetService.existsByEmailAndIsDeleted(email, false)) {
            throw ApplicationException.from(ALREADY_EXIST_EMAIL);
        }
    }

    public void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw ApplicationException.from(INVALID_NAME);
        }
    }

    public void validateNicknameFormat(String nickname) {
        if (nickname == null || nickname.isBlank()
            || !NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw ApplicationException.from(INVALID_NICKNAME);
        }
    }

    public void validateNicknameDuplicate(String nickname) {
        if (memberGetService.existsByNickname(nickname)) {
            throw ApplicationException.from(ALREADY_EXIST_NICKNAME);
        }
    }

    public void validateDepartment(String department) {
        if (department == null || department.isBlank()) {
            throw ApplicationException.from(INVALID_DEPARTMENT);
        }
    }

    public void validatePosition(String position) {
        if (position == null || position.isBlank()) {
            throw ApplicationException.from(INVALID_POSITION);
        }
    }

    public void validatePhoneFormat(String phone) {
        if (phone == null || phone.isBlank()
            || !PHONE_NUMBER_PATTERN.matcher(phone).matches()) {
            throw ApplicationException.from(INVALID_PHONE);
        }
    }

    public void validateRole(Role role) {
        if (role == null) {
            throw ApplicationException.from(INVALID_ROLE);
        }

        if (role.equals(Role.ADMIN)) {
            throw ApplicationException.from(AuthErrorCode.PERMISSION_DENIED);
        }
    }
}
