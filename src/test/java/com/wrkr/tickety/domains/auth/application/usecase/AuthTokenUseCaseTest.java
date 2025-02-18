package com.wrkr.tickety.domains.auth.application.usecase;

import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.auth.application.dto.response.AuthTokenResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.config.security.jwt.JwtProvider;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.utils.PkCrypto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthTokenUseCaseTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthTokenUseCase authTokenUseCase;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    @DisplayName("AuthToken UseCase Layer > 토큰 관리")
    class authToken {

        @Test
        @DisplayName("토큰 생성 요청에 성공하면 Access Token과 Refresh Token이 반환된다.")
        void successGenerateToken() {

            // given
            Member member = USER_A.toMember();
            String mockAccessToken = "mockAccessToken";
            String mockRefreshToken = "mockRefreshToken";

            when(jwtProvider.generateAccessToken(member.getNickname(), member.getRole())).thenReturn(mockAccessToken);
            when(jwtProvider.generateRefreshToken(member.getNickname(), member.getRole(), member.getMemberId())).thenReturn(mockRefreshToken);

            // when
            AuthTokenResponse response = authTokenUseCase.generateToken(member);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo(mockAccessToken);
            assertThat(response.refreshToken()).isEqualTo(mockRefreshToken);

            verify(jwtProvider, times(1)).generateAccessToken(member.getNickname(), member.getRole());
            verify(jwtProvider, times(1)).generateRefreshToken(member.getNickname(), member.getRole(), member.getMemberId());
        }

        @Test
        @DisplayName("토큰 재발급 요청에 성공하면 Refresh Token 검증 후 새로운 Access Token과 Refresh Token을 반환한다.")
        void successReissueToken() {
            // given
            Member member = USER_A.toMember();
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            String mockAccessToken = "newAccessToken";
            String mockRefreshToken = "newRefreshToken";

            when(jwtProvider.generateAccessToken(member.getNickname(), member.getRole())).thenReturn(mockAccessToken);
            when(jwtProvider.generateRefreshToken(member.getNickname(), member.getRole(), member.getMemberId())).thenReturn(mockRefreshToken);

            // when
            AuthTokenResponse response = authTokenUseCase.reissueToken(member, mockRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo(mockAccessToken);
            assertThat(response.refreshToken()).isEqualTo(mockRefreshToken);

            verify(jwtUtils, times(1)).validateRefreshToken(member.getMemberId(), mockRequest);
            verify(jwtProvider, times(1)).generateAccessToken(member.getNickname(), member.getRole());
            verify(jwtProvider, times(1)).generateRefreshToken(member.getNickname(), member.getRole(), member.getMemberId());
        }
    }
}
