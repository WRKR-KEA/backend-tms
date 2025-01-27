package com.wrkr.tickety.global.utils.date;

import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

public class TimePeriodExtractor {

    public static TimePeriod extractTimePeriod(LocalDateTime dateTime, StatisticsType statisticsType) {
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        switch (statisticsType) {
            case YEARLY -> {
                startDateTime = dateTime.withDayOfYear(1).toLocalDate().atStartOfDay();
                endDateTime = dateTime.plusYears(1).withDayOfYear(1).toLocalDate().atStartOfDay();
            }
            case MONTHLY -> {
                startDateTime = dateTime.withDayOfMonth(1).toLocalDate().atStartOfDay();
                endDateTime = dateTime.plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay();
            }
            case DAILY -> {
                startDateTime = dateTime.toLocalDate().atStartOfDay();
                endDateTime = dateTime.toLocalDate().plusDays(1).atStartOfDay();
            }
            case TOTAL -> {
                // datetime 최솟값: 1000-01-01 00:00:00.000000
                startDateTime = LocalDateTime.of(1000, 1, 1, 0, 0);
                endDateTime = LocalDateTime.now();
            }
            default -> {
                throw new ApplicationException(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID);
            }
        }

        return new TimePeriod(startDateTime, endDateTime);
    }

    @Getter
    @Builder
    public static class TimePeriod {

        private final LocalDateTime startDateTime;
        private final LocalDateTime endDateTime;

        public TimePeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }
    }
}