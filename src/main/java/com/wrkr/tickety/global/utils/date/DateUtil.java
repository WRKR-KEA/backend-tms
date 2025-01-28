package com.wrkr.tickety.global.utils.date;

import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class DateUtil {

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

    public static LocalDate convertToLocalDate(String date) {
        if (date == null || date.isEmpty()) {
            return LocalDate.now();
        }

        try {
            if (date.matches("\\d{4}")) { // 예: 2025
                return LocalDate.parse(date + "-01-01");
            } else if (date.matches("\\d{4}-\\d{2}")) { // 예: 2025-01
                return LocalDate.parse(date + "-01");
            } else {
                return LocalDate.parse(date); // 기본 ISO 형식
            }
        } catch (DateTimeParseException e) {
            throw new ApplicationException(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
    }
}