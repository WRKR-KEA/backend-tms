package com.wrkr.tickety.domains.member.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER("사용자"),
    MANAGER("담당자"),
    ADMIN("관리자")
    ;

    private final String description;
}

