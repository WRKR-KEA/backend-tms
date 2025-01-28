package com.wrkr.tickety.domains.ticket.domain.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.wrkr.tickety.domains.ticket.exception.StatisticsErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatisticsType {
    HOURLY("hourly"),
    DAILY("daily"),
    MONTHLY("monthly"),
    YEARLY("yearly");

    private final String uriValue;

    @JsonCreator
    public static StatisticsType from(String type) {

        return Arrays.stream(StatisticsType.values())
            .filter(statisticsType -> statisticsType.uriValue.equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> ApplicationException.from(StatisticsErrorCode.ILLEGAL_STATISTICS_OPTION));
    }
}
