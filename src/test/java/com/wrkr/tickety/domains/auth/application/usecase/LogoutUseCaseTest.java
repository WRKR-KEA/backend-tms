package com.wrkr.tickety.domains.auth.application.usecase;

import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_A;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private LogoutUseCase logoutUseCase;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    @DisplayName("Logout UseCase Layer > 로그아웃")
    class logout {

        @Test
        @DisplayName("로그아웃 요청에 성공하면 Refresh Token이 만료된다.")
        void successLogout() {
            // given
            Member member = USER_A.toMember();

            // when
            logoutUseCase.logout(member);

            // then
            verify(jwtUtils, times(1)).expireRefreshToken(member.getMemberId());
        }
    }
}
