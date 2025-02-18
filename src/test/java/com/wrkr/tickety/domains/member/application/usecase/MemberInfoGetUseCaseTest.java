package com.wrkr.tickety.domains.member.application.usecase;

import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_J;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("회원 정보 상세 조회 UseCase Layer Test")
public class MemberInfoGetUseCaseTest {

    @InjectMocks
    private MemberInfoGetUseCase memberInfoGetUseCase;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private PkCrypto pkCrypto;


    @BeforeEach
    public void setUp() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }


    @Test
    @DisplayName("회원 조회를 시도하여, 존재하는 회원일 경우 조회에 성공한다.")
    public void getMemberInfoSuccess() {
        // given
        Member member = USER_J.toMember();
        String encryptedMemberId = PkCrypto.encrypt(member.getMemberId());
        Long decryptedMemberId = PkCrypto.decrypt(encryptedMemberId);

        given(memberGetService.byMemberId(decryptedMemberId)).willReturn(member);

        // when
        MemberInfoResponse response = memberInfoGetUseCase.getMemberInfo(encryptedMemberId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(member.getEmail());
        assertThat(response.name()).isEqualTo(member.getName());
        assertThat(response.nickname()).isEqualTo(member.getNickname());
        assertThat(response.department()).isEqualTo(member.getDepartment());
        assertThat(response.position()).isEqualTo(member.getPosition());
        assertThat(response.phone()).isEqualTo(member.getPhone());
        assertThat(response.role()).isEqualTo(member.getRole().toString());
        assertThat(response.agitUrl()).isEqualTo(member.getAgitUrl());
    }

    @Test
    @DisplayName("존재하지 않는 회원일 경우 MEMBER_NOT_FOUND 예외를 반환한다.")
    public void getMemberInfoNotFound() {
        // given
        Member member = USER_J.toMember();
        String encryptedMemberId = PkCrypto.encrypt(member.getMemberId());
        Long decryptedMemberId = PkCrypto.decrypt(encryptedMemberId);

        // Mocking
        given(memberGetService.byMemberId(decryptedMemberId)).willThrow(ApplicationException.from(MEMBER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> {
            memberInfoGetUseCase.getMemberInfo(encryptedMemberId);
        }).isInstanceOf(ApplicationException.class)
            .hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
    }
}
