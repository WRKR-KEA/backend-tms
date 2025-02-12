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
            .nickname(member.getNickname())
            .name(member.getName())
            .role(member.getRole())
            .profileImage(member.getProfileImage())
            .isTempPassword(member.getIsTempPassword())
            .build();
    }
}
