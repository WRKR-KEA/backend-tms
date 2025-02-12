package com.wrkr.tickety.domains.member.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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
            .agitUrl("https://www.gachon.ac.kr")
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
        verify(memberPersistenceAdapter, times(1)).save(any(Member.class));
        assertThat(savedMember).isEqualTo(createdMember);
    }
}