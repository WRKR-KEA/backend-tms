package com.wrkr.tickety.domains.member.domain.service;

import com.wrkr.tickety.domains.auth.exception.AuthErrorCode;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.member.persistence.adapter.MemberPersistenceAdapter;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberGetService {

    private final MemberPersistenceAdapter memberPersistenceAdapter;

    public Member byMemberId(Long memberId) {
        return memberPersistenceAdapter.findById(memberId)
            .orElseThrow(() -> ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Page<Member> searchMember(
        ApplicationPageRequest pageRequest,
        Role role,
        String query
    ) {
        return memberPersistenceAdapter.searchMember(
            pageRequest,
            role,
            query
        );
    }

    public Boolean existsByEmail(String email) {
        return memberPersistenceAdapter.existsByEmail(email);
    }

    public Boolean existsByNickname(String nickname) {
        return memberPersistenceAdapter.existsByNickname(nickname);
    }

    public Member loadMemberByNickname(String nickname) {
        return memberPersistenceAdapter.findByNicknameAndIsDeleted(nickname)
            .orElseThrow(() -> ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getMemberByNickname(String nickname) {
        return memberPersistenceAdapter.findByNicknameAndIsDeleted(nickname)
            .orElseThrow(() -> ApplicationException.from(AuthErrorCode.INVALID_CREDENTIALS));
    }

    public List<Member> getAllManagers() {
        return memberPersistenceAdapter.getAllManagers();
    }

    public Page<Member> getAllManagersPage(ApplicationPageRequest pageRequest) {
        return memberPersistenceAdapter.getAllManagersPage(pageRequest);
    }
}
