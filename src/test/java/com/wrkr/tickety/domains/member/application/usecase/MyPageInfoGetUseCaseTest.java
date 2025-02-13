package com.wrkr.tickety.domains.member.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MyPageInfoGetUseCaseTest {

    @Mock
    private MemberGetService memberGetService;

    @InjectMocks
    private MyPageInfoGetUseCase myPageInfoGetUseCase;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Test
    @DisplayName("✅ 정상 케이스 - 사용자/담당자의 마이페이지 정보를 성공적으로 가져온다.")
    void getMyPageInfo() {
        // given
        Long memberId = 1L;
        Member member = Member.builder()
            .memberId(memberId)
            .nickname("nickname")
            .password("password")
            .name("name")
            .phone("010-1234-5678")
            .email("email@naver.com")
            .department("department")
            .position("position")
            .profileImage("profileImage")
            .role(Role.USER)
            .isDeleted(false)
            .build();

        given(memberGetService.byMemberId(memberId)).willReturn(member);

        //when
        MyPageInfoResponse response = myPageInfoGetUseCase.getMyPageInfo(memberId);

        //then
        assertThat(response.memberId()).isEqualTo(PkCrypto.encrypt(memberId));
        assertThat(response.nickname()).isEqualTo(member.getNickname());
        assertThat(response.name()).isEqualTo(member.getName());
    }

    @Test
    @DisplayName("❌ 실패 케이스 - 존재하지 않는 회원 ID로 조회 시 예외 발생 (MEMBER_NOT_FOUND)")
    void getMyPageInfo_memberNotFound() {
        // given
        Long invalidMemberId = 999L;

        given(memberGetService.byMemberId(invalidMemberId))
            .willThrow(ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> myPageInfoGetUseCase.getMyPageInfo(invalidMemberId))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("❌ 실패 케이스 - 삭제된 회원 정보 조회 시 예외 발생 (DELETED_MEMBER)")
    void getMyPageInfo_deletedMember() {
        // given
        Long memberId = 2L;
        Member deletedMember = Member.builder()
            .memberId(memberId)
            .nickname("nickname")
            .password("password")
            .name("name")
            .phone("010-1234-5678")
            .email("email@naver.com")
            .department("department")
            .position("position")
            .profileImage("profileImage")
            .role(Role.USER)
            .isDeleted(true)  // 삭제된 상태
            .build();

        given(memberGetService.byMemberId(memberId)).willReturn(deletedMember);

        // when & then
        assertThatThrownBy(() -> myPageInfoGetUseCase.getMyPageInfo(memberId))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MemberErrorCode.DELETED_MEMBER.getMessage());
    }
}