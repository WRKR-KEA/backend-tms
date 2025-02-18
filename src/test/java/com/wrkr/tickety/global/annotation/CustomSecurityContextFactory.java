package com.wrkr.tickety.global.annotation;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class CustomSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Member member = Member.builder()
            .memberId(annotation.memberId() != 0L ? annotation.memberId() : 1L)
            .nickname(annotation.nickname() != null ? annotation.nickname() : "defaultNickname")
            .role(annotation.role() != null ? annotation.role() : Role.USER)
            .name(annotation.username() != null ? annotation.username() : "defaultUser")
            .build();

        if (member.getMemberId() == 0) {
            throw new IllegalArgumentException("memberId는 0이 될 수 없습니다.");
        }

        context.setAuthentication(
            new UsernamePasswordAuthenticationToken(member, "PASSWORD", Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())))
        );

        return context;
    }
}
