package com.wrkr.tickety.global.config.security.auth;

import static com.wrkr.tickety.domains.member.domain.constant.Role.ADMIN;
import static com.wrkr.tickety.domains.member.domain.constant.Role.MANAGER;
import static com.wrkr.tickety.domains.member.domain.constant.Role.USER;

import com.wrkr.tickety.domains.member.domain.model.Member;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final transient Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (ADMIN.equals(member.getRole())) {
            return Collections.singletonList(new SimpleGrantedAuthority(ADMIN.name()));
        }
        if (MANAGER.equals(member.getRole())) {
            return Collections.singletonList(new SimpleGrantedAuthority(MANAGER.name()));
        }
        return Collections.singletonList(new SimpleGrantedAuthority(USER.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return member.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
