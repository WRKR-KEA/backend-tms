package com.wrkr.tickety.domains.member.application.usecase;

import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_J;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MemberInfoGetUseCaseTest {

    @InjectMocks
    private MemberInfoGetUseCase memberInfoGetUseCase;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private PkCrypto pkCrypto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    @DisplayName("GetMemberInfo UseCase Layer > 회원 조회")
    class GetMemberInfo {

        @Test
        @DisplayName("회원 조회를 시도하여, 존재하는 회원일 경우 조회에 성공한다.")
        public void getMemberInfoSuccess() {
            // given
            Member member = USER_J.toMember();
            String encryptedMemberId = PkCrypto.encrypt(member.getMemberId());
            Long decryptedMemberId = PkCrypto.decrypt(encryptedMemberId);

            MemberInfoResponse expectedResponse = MemberMapper.mapToMemberInfoResponse(member);

            given(memberGetService.byMemberId(decryptedMemberId)).willReturn(member);

            // when
            MemberInfoResponse response = memberInfoGetUseCase.getMemberInfo(encryptedMemberId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.email()).isEqualTo(expectedResponse.email());
            assertThat(response.name()).isEqualTo(expectedResponse.name());
            assertThat(response.nickname()).isEqualTo(expectedResponse.nickname());
            assertThat(response.department()).isEqualTo(expectedResponse.department());
            assertThat(response.position()).isEqualTo(expectedResponse.position());
            assertThat(response.phone()).isEqualTo(expectedResponse.phone());
            assertThat(response.role()).isEqualTo(expectedResponse.role());
            assertThat(response.agitUrl()).isEqualTo(expectedResponse.agitUrl());
        }

        @Test
        @DisplayName("존재하지 않는 회원일 경우 MEMBER_NOT_FOUND 예외를 반환한다.")
        public void getMemberInfoNotFound() {
            // given
            Member member = USER_J.toMember();
            String encryptedMemberId = PkCrypto.encrypt(member.getMemberId());
            Long decryptedMemberId = PkCrypto.decrypt(encryptedMemberId);

            // Mocking
            given(memberGetService.byMemberId(decryptedMemberId)).willThrow(new ApplicationException(MEMBER_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> {
                memberInfoGetUseCase.getMemberInfo(encryptedMemberId);
            }).isInstanceOf(ApplicationException.class)
                .hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
        }
    }
}
