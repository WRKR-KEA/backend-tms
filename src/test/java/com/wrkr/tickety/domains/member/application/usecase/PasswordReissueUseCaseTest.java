package com.wrkr.tickety.domains.member.application.usecase;

import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_J;
import static com.wrkr.tickety.domains.auth.exception.AuthErrorCode.INVALID_VERIFICATION_CODE;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberSaveService;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailUtil;
import com.wrkr.tickety.infrastructure.redis.RedisService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PasswordReissueUseCaseTest {

    @InjectMocks
    private PasswordReissueUseCase passwordReissueUseCase;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private MemberSaveService memberSaveService;

    @Mock
    private RedisService redisService;

    @Mock
    private EmailUtil emailUtil;

    @Mock
    private PkCrypto pkCrypto;

    @BeforeEach
    public void setUp() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Test
    @DisplayName("유효한 인증 코드가 제공되면 비밀번호가 재발급되고 이메일이 발송된다.")
    void reissuePasswordSuccess() {
        // given
        Member member = USER_J.toMember();
        String encryptedMemberId = PkCrypto.encrypt(member.getMemberId());
        String verificationCodePrefix = "verification-code-";
        String verificationCode = "validVerificationCode";
        String tempPassword = "newTempPassword123";

        when(redisService.getValues(anyString())).thenReturn(Optional.of(verificationCode));
        when(memberGetService.byMemberId(any())).thenReturn(member);
        when(memberSaveService.save(any())).thenReturn(member);
        doNothing().when(emailUtil).sendMail(any(), eq(tempPassword), eq(EmailConstants.FILENAME_PASSWORD));
        doNothing().when(redisService).deleteValues(eq(verificationCodePrefix + encryptedMemberId));

        // when
        MemberPkResponse response = passwordReissueUseCase.reissuePassword(encryptedMemberId, verificationCode);

        // then
        assertThat(response.memberId()).isEqualTo(PkCrypto.encrypt(member.getMemberId()));
        verify(redisService, times(1)).deleteValues(verificationCode + encryptedMemberId);
        verify(emailUtil, times(1)).sendMail(any(), eq(tempPassword), eq(EmailConstants.FILENAME_PASSWORD));
    }

    @Test
    @DisplayName("제공된 인증 코드가 저장된 인증 코드와 다르면 INVALID_VERIFICATION_CODE 예외가 발생한다.")
    void reissuePasswordInvalidVerificationCode() {
        // given
        Member member = USER_J.toMember();
        String encryptedMemberId = PkCrypto.encrypt(member.getMemberId());
        String invalidVerificationCode = "invalidVerificationCode";
        String validVerificationCode = "validVerificationCode";

        when(redisService.getValues(anyString())).thenReturn(Optional.of(validVerificationCode));

        // when & then
        assertThatThrownBy(() -> {
            passwordReissueUseCase.reissuePassword(encryptedMemberId, invalidVerificationCode);
        }).isInstanceOf(ApplicationException.class)
            .hasMessage(INVALID_VERIFICATION_CODE.getMessage());
    }

    @Test
    @DisplayName("회원이 존재하지 않으면 예외가 발생한다.")
    void reissuePasswordMemberNotFound() {
        // given
        String encryptedMemberId = PkCrypto.encrypt(999L);
        String verificationCode = "validVerificationCode";

        when(redisService.getValues(verificationCode + encryptedMemberId)).thenReturn(Optional.of(verificationCode));
        when(memberGetService.byMemberId(any())).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> {
            passwordReissueUseCase.reissuePassword(encryptedMemberId, verificationCode);
        }).isInstanceOf(ApplicationException.class)
            .hasMessage(MEMBER_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("인증 코드가 Redis에 존재하지 않을 경우 INVALID_VERIFICATION_CODE 예외가 발생한다.")
    void reissuePasswordRedisNotFound() {
        // given
        Member member = USER_J.toMember();
        String encryptedMemberId = PkCrypto.encrypt(member.getMemberId());
        String verificationCode = "validVerificationCode";

        when(redisService.getValues(verificationCode + encryptedMemberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> {
            passwordReissueUseCase.reissuePassword(encryptedMemberId, verificationCode);
        }).isInstanceOf(ApplicationException.class)
            .hasMessage(INVALID_VERIFICATION_CODE.getMessage());
    }

}