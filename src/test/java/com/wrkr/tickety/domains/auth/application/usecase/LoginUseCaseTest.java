package com.wrkr.tickety.domains.auth.application.usecase;

import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.common.fixture.member.UserFixture;
import com.wrkr.tickety.domains.auth.application.dto.request.LoginRequest;
import com.wrkr.tickety.domains.auth.exception.AuthErrorCode;
import com.wrkr.tickety.domains.auth.utils.LoginAttemptHandler;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginUseCaseTest {

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private LoginAttemptHandler loginAttemptHandler;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    @DisplayName("Login UseCase Layer > 로그인")
    class login {

        @Test
        @DisplayName("계정이 잠겨 있을 경우 ACCOUNT_LOCKED 예외가 발생한다.")
        void throwExceptionByAccountLocked() {
            // given
            Member member = USER_A.toMember();
            LoginRequest loginRequest = new LoginRequest(member.getEmail(), member.getPassword());

            given(memberGetService.getMemberByNickname(loginRequest.nickname())).willReturn(member);
            given(loginAttemptHandler.isAccountLocked(member.getMemberId())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> loginUseCase.login(loginRequest))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(AuthErrorCode.ACCOUNT_LOCKED.getMessage());

            verify(loginAttemptHandler, never()).handleFailedAttempt(anyLong());
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않을 경우 INVALID_CREDENTIALS 예외가 발생하고 실패 횟수가 증가한다.")
        void throwExceptionByInvalidCredentials() {
            // given
            Member member = UserFixture.USER_A.toMember();
            LoginRequest loginRequest = new LoginRequest(member.getNickname(), "wrong-password");

            given(memberGetService.getMemberByNickname(loginRequest.nickname())).willReturn(member);
            given(loginAttemptHandler.isAccountLocked(member.getMemberId())).willReturn(false);

            // when - then
            assertThatThrownBy(() -> loginUseCase.login(loginRequest))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(AuthErrorCode.INVALID_CREDENTIALS.getMessage());

            verify(loginAttemptHandler, times(1)).handleFailedAttempt(member.getMemberId());
        }

        @Test
        @DisplayName("로그인 요청에 성공하면 Member 객체가 반환된다.")
        void successLogin() {
            // given
            Member member = UserFixture.USER_A.toMember();
            String password = USER_A.getRawPassword();
            LoginRequest loginRequest = new LoginRequest(member.getNickname(), password);

            given(memberGetService.getMemberByNickname(loginRequest.nickname())).willReturn(member);
            given(loginAttemptHandler.isAccountLocked(member.getMemberId())).willReturn(false);
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

            // when
            Member result = loginUseCase.login(loginRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getNickname()).isEqualTo(member.getNickname());

            verify(loginAttemptHandler, times(1)).resetFailedAttempts(member.getMemberId());
        }
    }
}
