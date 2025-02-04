package com.wrkr.tickety.domains.auth.application.usecase;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class LogoutUseCase {

    private final JwtUtils jwtUtils;

    public void logout(Member member) {
        jwtUtils.expireRefreshToken(member.getMemberId());
    }
}
