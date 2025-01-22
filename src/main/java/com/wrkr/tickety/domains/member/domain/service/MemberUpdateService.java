package com.wrkr.tickety.domains.member.domain.service;

import com.wrkr.tickety.domains.member.persistence.entity.Member;
import com.wrkr.tickety.domains.member.persistence.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberUpdateService {
    private final MemberRepository memberRepository;

    public void modifyMemberInfo(Member modifiedMember) {
        memberRepository.save(modifiedMember);
    }
}
