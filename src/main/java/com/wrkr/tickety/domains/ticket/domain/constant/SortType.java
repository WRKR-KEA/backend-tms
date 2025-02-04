package com.wrkr.tickety.domains.ticket.domain.constant;


import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
    NEWEST("최신순"),
    OLDEST("오래된 순"),
    UPDATED("업데이트 순"),
    ;

    private final String description;

    @JsonCreator
    public static SortType from(String type) {
        return Arrays.stream(values())
            .filter(statisticsType -> statisticsType.name().equalsIgnoreCase(type))
            .findFirst()
            .orElse(UPDATED);
    }
}
