package com.wrkr.tickety.domains.ticket.domain.constant;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
    NEWEST("newest"),
    OLDEST("oldest");

    private final String uriValue;

    @JsonCreator
    public static SortType from(String type) {
        return Arrays.stream(com.wrkr.tickety.domains.ticket.domain.constant.SortType.values())
            .filter(statisticsType -> statisticsType.uriValue.equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> ApplicationException.from(CommonErrorCode.BAD_REQUEST));
    }
}
