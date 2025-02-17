package com.wrkr.tickety.domains.member.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import com.wrkr.tickety.domains.member.persistence.mapper.MemberPersistenceMapper;
import com.wrkr.tickety.domains.member.persistence.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberPersistenceAdapterTest {

    @InjectMocks
    private MemberPersistenceAdapter memberPersistenceAdapter;

    @Mock
    private MemberPersistenceMapper memberPersistenceMapper;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("회원 도메인 객체를 회원 엔티티로 변환하고 저장한다.")
    @Test
    void saveTest() {
        //given
        MemberEntity memberEntity = MemberEntity.builder()
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

        Member createdMember = Member.builder()
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

        when(memberPersistenceMapper.toEntity(createdMember)).thenReturn(memberEntity);
        when(memberRepository.save(memberEntity)).thenReturn(memberEntity);
        when(memberPersistenceMapper.toDomain(memberEntity)).thenReturn(createdMember);

        //when
        Member savedMember = memberPersistenceAdapter.save(createdMember);

        //then
        assertThat(savedMember).isEqualTo(createdMember);
    }
}