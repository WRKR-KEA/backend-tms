package com.wrkr.tickety.domains.member.domain.service;

import com.wrkr.tickety.domains.member.persistence.entity.Member;
import com.wrkr.tickety.domains.member.persistence.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCreateService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }
}
