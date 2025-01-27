package com.wrkr.tickety.usecase;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.member.application.dto.request.MyPageInfoUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.application.usecase.MyPageInfoGetUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MyPageInfoUpdateUseCase;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberUpdateService;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MyPageUseCaseTest {

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private MemberUpdateService memberUpdateService;

    @InjectMocks
    private MyPageInfoGetUseCase myPageInfoGetUseCase;

    @InjectMocks
    private MyPageInfoUpdateUseCase myPageInfoUpdateUseCase;

    private static final Long MEMBER_ID = 1L;

    private Member member;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .memberId(MEMBER_ID)
            .password("password")
            .nickname("사용자")
            .email("email@naver.com")
            .phone("전화번호")
            .name("사용자")
            .position("직책")
            .role(Role.USER)
            .isDeleted(false)
            .build();
    }

    @Test
    @DisplayName("공통 - 마이페이지 회원 정보 조회를 한다")
    void getMyPageInfo() {
        // given
        given(memberGetService.byMemberId(MEMBER_ID)).willReturn(Optional.of(member));

        // when
        MyPageInfoResponse response = myPageInfoGetUseCase.getMyPageInfo(MEMBER_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.memberId()).isEqualTo(PkCrypto.encrypt(MEMBER_ID));
        assertThat(response.nickname()).isEqualTo("사용자");

        verify(memberGetService).byMemberId(MEMBER_ID);
    }

    @Test
    @DisplayName("공통 - 마이페이지 회원 정보 수정을 한다")
    void updateMyPageInfo() {
        // given
        MyPageInfoUpdateRequest request = MyPageInfoUpdateRequest.builder()
            .position("수정된 직책")
            .phone("수정된 전화번호")
            .build();

        Member modifiedMember = Member.builder()
            .memberId(MEMBER_ID)
            .password("password")
            .nickname("사용자")
            .email("email@naver.com")
            .phone("수정된 전화번호")
            .name("사용자")
            .position("수정된 직책")
            .role(Role.USER)
            .isDeleted(false)
            .build();

        given(memberGetService.byMemberId(MEMBER_ID)).willReturn(Optional.of(member));
        given(memberUpdateService.modifyMemberInfo(member)).willReturn(modifiedMember);

        //when
        MemberPkResponse response = myPageInfoUpdateUseCase.updateMyPageInfo(MEMBER_ID, request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.memberId()).isEqualTo(PkCrypto.encrypt(MEMBER_ID));

        verify(memberGetService).byMemberId(MEMBER_ID);
        verify(memberUpdateService).modifyMemberInfo(member);
    }
}
