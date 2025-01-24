package com.wrkr.tickety.domains.member.domain.service;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.adapter.MemberPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberGetService {

    private final MemberPersistenceAdapter memberPersistenceAdapter;

    public Optional<Member> byMemberId(Long memberId) {
        return memberPersistenceAdapter.findById(memberId);
    }

    public Page<Member> pagingByRole(Pageable pageable, Role role) {
        return memberPersistenceAdapter.pagingByRole(pageable, role);
    }

    public Boolean existsByEmail(String email) {
        return memberPersistenceAdapter.existsByEmail(email);
    }

    public Boolean existsByNickname(String nickname) {
        return memberPersistenceAdapter.existsByNickname(nickname);
    }
}
