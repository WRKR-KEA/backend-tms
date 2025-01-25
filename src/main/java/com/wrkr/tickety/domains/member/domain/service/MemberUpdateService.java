package com.wrkr.tickety.domains.member.domain.service;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.adapter.MemberPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberUpdateService {
    private final MemberPersistenceAdapter memberPersistenceAdapter;

    public Member modifyMemberInfo(Member modifiedMember) {
        return memberPersistenceAdapter.save(modifiedMember);
    }
}
