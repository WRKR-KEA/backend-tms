package com.wrkr.tickety.domains.member.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.member.persistence.adapter.MemberPersistenceAdapter;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberGetServiceTest {

    @Mock
    MemberPersistenceAdapter memberPersistenceAdapter;

    @InjectMocks
    MemberGetService memberGetService;

    private Member createdMember;

    @BeforeEach
    void setUp() {
        createdMember = Member.builder()
            .memberId(1L)
            .nickname("testUser")
            .password("User1234!")
            .name("Test User")
            .phone("010-1234-5678")
            .email("test@gachon.ac.kr")
            .department("BE team1")
            .position("Developer")
            .profileImage("profile.jpg")
            .role(Role.USER)
            .build();
    }

    @DisplayName("조회된 회원 도메인 객체를 검증한다.")
    @Test
    void byMemberIdTest() {
        //given
        given(memberPersistenceAdapter.findById(any(Long.class))).willReturn(Optional.ofNullable(createdMember));
        //when
        Member findMember = memberGetService.byMemberId(1L);
        //then
        verify(memberPersistenceAdapter).findById(any(Long.class));
        assertThat(findMember).isEqualTo(createdMember);
    }

    @DisplayName("존재하지 않는 회원을 조회했을 경우 발생하는 예외를 검증한다.")
    @Test
    void byMemberIdWithNotFoundException() {
        //given
        Long memberId = 2L;
        given(memberPersistenceAdapter.findById(memberId)).willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> memberGetService.byMemberId(memberId))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage())
            .satisfies(exception -> {
                // 예외를 ApplicationException으로 캐스팅
                assertThat(((ApplicationException) exception).getCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
            });
    }
}