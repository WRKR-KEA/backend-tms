package com.wrkr.tickety.domains.auth.application.mapper;

import com.wrkr.tickety.domains.auth.application.dto.response.TokenResponse;

public class AuthMapper {

    private AuthMapper() {
        throw new IllegalArgumentException();
    }

    public static TokenResponse toTokenResponse(String accessToken, String refreshToken) {
        return TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
