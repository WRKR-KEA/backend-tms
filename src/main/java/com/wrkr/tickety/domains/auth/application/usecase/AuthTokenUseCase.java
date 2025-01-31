package com.wrkr.tickety.domains.auth.application.usecase;

import static com.wrkr.tickety.domains.auth.application.mapper.AuthMapper.toTokenResponse;

import com.wrkr.tickety.domains.auth.application.dto.response.TokenResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.config.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class AuthTokenUseCase {

    private final JwtProvider jwtProvider;

    public TokenResponse generateToken(Member member) {
        String accessToken = jwtProvider.generateAccessToken(member.getNickname(), member.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(member.getNickname(), member.getRole(), member.getMemberId());

        return toTokenResponse(accessToken, refreshToken);
    }
}
