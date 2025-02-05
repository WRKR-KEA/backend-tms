package com.wrkr.tickety.domains.member.persistence.adapter;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import com.wrkr.tickety.domains.member.persistence.mapper.MemberPersistenceMapper;
import com.wrkr.tickety.domains.member.persistence.repository.MemberRepository;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
        final Optional<MemberEntity> memberEntity = this.memberRepository.findById(memberId);
        return memberEntity.map(this.memberPersistenceMapper::toDomain);
    }

    public Optional<Member> findByNicknameAndIsDeleted(final String nickname) {
        final Optional<MemberEntity> memberEntity = this.memberRepository.findByNicknameAndIsDeleted(nickname, false);
        return memberEntity.map(this.memberPersistenceMapper::toDomain);
    }

    public Page<Member> searchMember(
        final ApplicationPageRequest pageRequest,
        final Role role,
        final String email,
        final String name,
        final String department) {

        Pageable pageable = pageRequest.toPageableNoSort();

        Page<MemberEntity> memberEntityPage = memberRepository.searchMember(pageable, role, email, name, department);

        return memberEntityPage.map(this.memberPersistenceMapper::toDomain);
    }

    public Boolean existsByEmail(final String email) {
        return this.memberRepository.existsByEmail(email);
    }

    public Boolean existsByNickname(final String nickname) {
        return this.memberRepository.existsByNickname(nickname);
    }
}
