package com.wrkr.tickety.domains.member.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.adapter.MemberPersistenceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberSaveServiceTest {

    @Mock
    MemberPersistenceAdapter memberPersistenceAdapter;

    @InjectMocks
    MemberSaveService memberSaveService;

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

    @DisplayName("신규 회원이 등록되면 저장된 회원 정보를 검증한다.")
    @Test
    void memberSaveTest() {
        //given
        given(memberPersistenceAdapter.save(any(Member.class))).willReturn(createdMember);

        //when
        Member savedMember = memberSaveService.save(createdMember);

        //then
        verify(memberPersistenceAdapter).save(any(Member.class));
        assertThat(savedMember).isEqualTo(createdMember);
    }

    @DisplayName("이미 존재하는 닉네임으로 회원을 등록할 수 없다.")
    @Test
    void memberSaveTestWithDuplicateNickname() {
        //given

        //when

        //then
    }

    @DisplayName("등록하려는 회원의 이메일이 중복될 때 발생하는 예외를 검증한다.")
    @Test
    void memberSaveTestWithDuplicateEmail() {
        //given

        //when

        //then
    }
}