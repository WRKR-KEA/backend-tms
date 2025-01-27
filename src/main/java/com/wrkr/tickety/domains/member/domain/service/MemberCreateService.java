package com.wrkr.tickety.domains.member.domain.service;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.adapter.MemberPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCreateService {
    private final MemberPersistenceAdapter memberPersistenceAdapter;

    @Transactional
    public Member createMember(Member member) {
        return memberPersistenceAdapter.save(member);
    }
}
