package com.wrkr.tickety.domains.log.domain.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@RequiredArgsConstructor
public enum ActionType {

    LOGIN("로그인"),
    LOGOUT("로그아웃"),
    REFRESH("리프레쉬");

    private final String description;

    @JsonCreator
    public static ActionType from(String param) {
        for (ActionType type : ActionType.values()) {
            if (type.name().equals(param)) {
                return type;
            }
        }
        return null;
    }
}
