package com.wrkr.tickety.domains.member.domain.service;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.adapter.MemberPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberGetService {

    private final MemberPersistenceAdapter memberPersistenceAdapter;

    public Optional<Member> getUserById(Long userId) {
        return memberPersistenceAdapter.findById(userId);
    }
}
