package com.wrkr.tickety.domains.ticket.domain.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatisticsType {
    DAILY("daily"),
    MONTHLY("monthly"),
    YEARLY("yearly"),
    TOTAL("total");

    private final String uriValue;

    @JsonCreator
    public static StatisticsType from(String type) {
        return Arrays.stream(StatisticsType.values())
            .filter(statisticsType -> statisticsType.uriValue.equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + type));
    }
}