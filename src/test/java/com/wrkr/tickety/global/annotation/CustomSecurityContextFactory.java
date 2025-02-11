package com.wrkr.tickety.global.annotation;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.config.security.auth.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class CustomSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Member member = Member.builder()
            .nickname(annotation.nickname())
            .role(annotation.role())
            .name(annotation.username())
            .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        context.setAuthentication(
            new UsernamePasswordAuthenticationToken(customUserDetails, "PASSWORD", customUserDetails.getAuthorities())
        );

        return context;
    }
}
