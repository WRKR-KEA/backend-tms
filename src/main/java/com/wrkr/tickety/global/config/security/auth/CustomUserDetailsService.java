package com.wrkr.tickety.global.config.security.auth;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.adapter.MemberPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberPersistenceAdapter memberPersistenceAdapter;

    @Override
    public CustomUserDetails loadUserByUsername(String nickname) {
        Member member = memberPersistenceAdapter.findByNicknameAndIsDeleted(nickname);
        return new CustomUserDetails(member);
    }
}
