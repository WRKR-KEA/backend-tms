package com.wrkr.tickety.domains.auth.application.mapper;

import com.wrkr.tickety.domains.auth.application.dto.response.AuthTokenResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;

public class AuthMapper {

    private AuthMapper() {
        throw new IllegalArgumentException();
    }

    public static AuthTokenResponse toAuthTokenResponse(String accessToken, String refreshToken, Member member) {
        return AuthTokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .name(member.getName())
            .role(member.getRole())
            .profileImage(member.getProfileImage())
            .build();
    }
}
