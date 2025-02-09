package com.wrkr.tickety.domains.auth.application.usecase;

import static com.wrkr.tickety.domains.auth.exception.AuthErrorCode.ACCOUNT_LOCKED;
import static com.wrkr.tickety.domains.auth.exception.AuthErrorCode.INVALID_CREDENTIALS;
import static com.wrkr.tickety.domains.auth.utils.PasswordEncoderUtil.verifyPassword;

import com.wrkr.tickety.domains.auth.application.dto.request.LoginRequest;
import com.wrkr.tickety.domains.auth.utils.LoginAttemptHandler;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class LoginUseCase {

    private final MemberGetService memberGetService;
    private final LoginAttemptHandler loginAttemptHandler;

    public Member login(LoginRequest loginRequest) {
        Member member = memberGetService.getMemberByNickname(loginRequest.nickname());

        validateLockedAccount(member.getMemberId());
        validatePassword(member, loginRequest.password());

        loginAttemptHandler.resetFailedAttempts(member.getMemberId());

        return member;
    }

    private void validateLockedAccount(Long memberId) {
        if (loginAttemptHandler.isAccountLocked(memberId)) {
            throw ApplicationException.from(ACCOUNT_LOCKED);
        }
    }

    private void validatePassword(Member member, String password) {
        if (!verifyPassword(password, member.getPassword())) {
            loginAttemptHandler.handleFailedAttempt(member.getMemberId());
            throw ApplicationException.from(INVALID_CREDENTIALS);
        }
    }
}