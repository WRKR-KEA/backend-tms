package com.wrkr.tickety.domains.ticket.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModifiedType {

    MANAGER("담당자 변경"),
    STATUS("상태 변경"),
    ;

    private final String description;
}
