package com.wrkr.tickety.domains.member.domain.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@RequiredArgsConstructor
public enum Role {

    USER("사용자"),
    MANAGER("담당자"),
    ADMIN("관리자");

    @JsonValue
    private final String description;

    @JsonCreator // Json -> Object, 역직렬화 수행하는 메서드
    public static Role from(String param) {
        String upperParam = param == null ? null : param.toUpperCase();

        for (Role role : Role.values()) {
            if (role.name().equals(upperParam)) {
                return role;
            }
        }
        log.error("Role.from() exception occur param: {}", param);
        return null;
    }
}
