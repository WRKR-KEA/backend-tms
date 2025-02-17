package com.wrkr.tickety.domains.member.application.usecase;

import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_J;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.presentation.util.validator.MemberFieldValidator;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.infrastructure.email.EmailUtil;
import com.wrkr.tickety.infrastructure.redis.RedisService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("인증 코드 발급 UseCase Layer Test")
public class VerificationCodeCreateUseCaseTest {

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private RedisService redisService;

    @Mock
    private EmailUtil emailUtil;

    @Mock
    private MemberFieldValidator memberFieldValidator;

    @InjectMocks
    private VerificationCodeCreateUseCase verificationCodeCreateUseCase;

    @Mock
    private PkCrypto pkCrypto;

    @BeforeEach
    public void setUp() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Test
    @DisplayName("유효한 아이디(닉네임)일 경우 인증 코드가 이메일로 전송되며, Redis에 적절한 key 값으로 저장된다.")
    void createVerificationCodeSuccess() {
        // given
        String verificationCodePrefix = "verification-code-";
        Member member = USER_J.toMember();
        String encryptedMemberId = PkCrypto.encrypt(member.getMemberId());

        doNothing().when(memberFieldValidator).validateNicknameFormat(anyString());

        when(memberGetService.findMemberByNickname(member.getNickname())).thenReturn(Optional.of(member));
        doNothing().when(redisService).deleteValues(anyString());
        doNothing().when(redisService).setValues(anyString(), anyString(), any());
        doNothing().when(emailUtil).sendMail(any(), anyString(), anyString());

        // when
        MemberPkResponse response = verificationCodeCreateUseCase.createVerificationCode(member.getNickname());

        // then
        assertThat(response.memberId()).isEqualTo(encryptedMemberId);
        verify(redisService, times(1)).deleteValues(eq(verificationCodePrefix + encryptedMemberId));
        verify(redisService, times(1)).setValues(eq(verificationCodePrefix + encryptedMemberId), anyString(), any());
        verify(emailUtil, times(1)).sendMail(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("회원이 존재하지 않는 경우 INVALID_NICKNAME 예외를 발생시킨다.")
    void createVerificationCodeMemberNotFound() {
        // given
        String nickname = "nonExistentNickname";

        //when & then
        assertThatThrownBy(() -> {
            verificationCodeCreateUseCase.createVerificationCode(nickname);
        }).isInstanceOf(ApplicationException.class)
            .hasMessageContaining(INVALID_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("닉네임(아이디) 형식이 올바르지 않은 경우 INVALID_NICKNAME 예외를 발생시킨다.")
    void createVerificationCodeNicknameFormatIsInvalid() {
        // given
        String nickname = "invalid_nickname_format";

        doThrow(ApplicationException.from(INVALID_NICKNAME)).when(memberFieldValidator).validateNicknameFormat(nickname);

        //when & then
        assertThatThrownBy(() -> {
            verificationCodeCreateUseCase.createVerificationCode(nickname);
        }).isInstanceOf(ApplicationException.class)
            .hasMessageContaining(INVALID_NICKNAME.getMessage());
    }
}
