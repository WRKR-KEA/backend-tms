package com.wrkr.tickety.domains.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "JWT 토큰 응답 DTO")
@Builder
public record TokenResponse(

    @Schema(description = "엑세스 토큰", example = "eyJ0eXAiOiJKV1QiLCJhbG.eyJzdWIiOiIxQiOjE2OTgzODk0MjAsImV4cCI6MTY5OTU5OTAyMH0.XYB0Bl6UZ3xGgG1wnXd-ACE")
    String accessToken,

    @Schema(description = "리프레시 토큰", example = "eyJ0eXAiOiJKV1QiLCJhbG.eyJzdWIiOiIxQiOjE2OTgzODk0MjAsImV4cCI6MTY5OTU5OTAyMH0.XYB0Bl6UZ3xGgG1wnXd-REF")
    String refreshToken
) {

}
