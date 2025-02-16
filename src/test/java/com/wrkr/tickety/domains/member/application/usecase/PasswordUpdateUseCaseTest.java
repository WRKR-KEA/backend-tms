package com.wrkr.tickety.domains.member.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.auth.utils.PasswordEncoderUtil;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberSaveService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PasswordUpdateUseCaseTest {

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private MemberSaveService memberSaveService;

    @InjectMocks
    private PasswordUpdateUseCase passwordUpdateUseCase;

    @Mock
    private PkCrypto pkCrypto;

    @BeforeEach
    public void setUp() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    @DisplayName("PasswordUpdate UseCase Layer Test")
    class UpdatePasswordTest {

        @Test
        @DisplayName("비밀번호 변경이 성공적으로 수행되면 isTempPassword가 false로 변경된다.")
        void updatePasswordSuccess() {
            // given
            Long memberId = 1L;
            String newPassword = "newPassword123!";
            String encryptedMemberId = PkCrypto.encrypt(memberId);

            Member member = Member.builder()
                .memberId(memberId)
                .password("oldPassword!")
                .isTempPassword(true)
                .build();

            when(memberGetService.byMemberId(memberId)).thenReturn(member);
            when(memberSaveService.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            MemberPkResponse response = passwordUpdateUseCase.updatePassword(memberId, newPassword, newPassword);

            // then
            assertThat(response).isNotNull();
            assertThat(response.memberId()).isEqualTo(encryptedMemberId);
            assertThat(PasswordEncoderUtil.verifyPassword(newPassword, member.getPassword())).isTrue();
            assertThat(member.getIsTempPassword()).isFalse();

            verify(memberGetService, times(1)).byMemberId(memberId);
            verify(memberSaveService, times(1)).save(any(Member.class));
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않을 경우 UNMATCHED_PASSWORD 예외가 발생한다.")
        void updatePasswordUnmatchedException() {
            // given
            Long memberId = 1L;
            String password = "newPassword123!";
            String confirmPassword = "differentPassword123!";

            // when & then
            assertThatThrownBy(() -> passwordUpdateUseCase.updatePassword(memberId, password, confirmPassword))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(MemberErrorCode.UNMATCHED_PASSWORD.getMessage());

            verify(memberGetService, never()).byMemberId(anyLong());
            verify(memberSaveService, never()).save(any(Member.class));
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 요청 시 MEMBER_NOT_FOUND 예외가 발생한다.")
        void updatePasswordMemberNotFoundException() {
            // given
            Long memberId = 999L;
            String newPassword = "newPassword123!";

            when(memberGetService.byMemberId(memberId)).thenThrow(ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> passwordUpdateUseCase.updatePassword(memberId, newPassword, newPassword))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

            verify(memberGetService, times(1)).byMemberId(memberId);
            verify(memberSaveService, never()).save(any(Member.class));
        }
    }
}
