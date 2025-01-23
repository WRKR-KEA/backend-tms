package com.wrkr.tickety.domains.member.persistence.adapter;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import com.wrkr.tickety.domains.member.persistence.mapper.MemberPersistenceMapper;
import com.wrkr.tickety.domains.member.persistence.repository.MemberRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberPersistenceAdapter {

    private final MemberRepository memberRepository;
    private final MemberPersistenceMapper memberPersistenceMapper;

    public Member save(final Member member) {
        MemberEntity memberEntity = this.memberPersistenceMapper.toEntity(member);
        MemberEntity savedEntity = this.memberRepository.save(memberEntity);
        return this.memberPersistenceMapper.toDomain(savedEntity);
    }

    public Optional<Member> findById(final Long memberId) {
        final Optional<MemberEntity> memberEntity = Optional.ofNullable(this.memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(MemberErrorCode.MEMBER_NOT_FOUND)));
        return memberEntity.map(this.memberPersistenceMapper::toDomain);
    }

    public Boolean existsByEmail(final String email) {
        return this.memberRepository.existsByEmail(email);
    }

    public Boolean existsByNickname(final String nickname) {
        return this.memberRepository.existsByNickname(nickname);
    }
}
