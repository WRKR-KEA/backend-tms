package com.wrkr.tickety.domains.ticket.application.usecase.statistics;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse.TicketCount;
import com.wrkr.tickety.domains.ticket.application.mapper.StatisticsMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.domains.ticket.exception.StatisticsErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.date.DateUtil;
import com.wrkr.tickety.global.utils.date.TimePeriod;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsGetUseCase {

    private final TicketHistoryGetService ticketHistoryGetService;

    public StatisticsByTicketStatusResponse getTicketCountStatistics(String date, StatisticsType type, TicketStatus status) {
        LocalDate localDate = DateUtil.convertToLocalDate(date);
        TimePeriod timePeriod = DateUtil.extractTimePeriod(localDate.atStartOfDay(), type);

        List<TicketCount> ticketCountList = ticketHistoryGetService.getTicketCountStatistics(
            timePeriod.getStartDateTime(),
            timePeriod.getEndDateTime(),
            type,
            status
        );

        List<TicketCount> completeCountList = getCompleteCountList(
            ticketCountList,
            localDate,
            type
        );

        return StatisticsMapper.mapToStatisticsByTicketStatusResponse(
            date,
            completeCountList
        );
    }

    // targetDate 재가공 및 count가 0인 targetDate도 리스트에 포함
    private List<TicketCount> getCompleteCountList(
        List<TicketCount> ticketCountList,
        LocalDate localDate,
        StatisticsType statisticsType
    ) {
        List<TicketCount> completeCountList = new ArrayList<>();

        switch (statisticsType) {
            case TOTAL: {
                int baseYear = localDate.getYear();
                for (int year = baseYear - 5; year <= baseYear + 5; year++) {
                    String targetDate = String.valueOf(year);
                    Long count = getCountForTargetDate(ticketCountList, targetDate);
                    completeCountList.add(new TicketCount(targetDate, count));
                }
                break;
            }

            case YEARLY: {
                int baseYear = localDate.getYear();
                for (int month = 1; month <= 12; month++) {
                    String targetDate = String.format("%04d-%02d", baseYear, month);
                    Long count = getCountForTargetDate(ticketCountList, targetDate);
                    completeCountList.add(new TicketCount(String.valueOf(month), count));
                }
                break;
            }

            case MONTHLY: {
                int year = localDate.getYear();
                int month = localDate.getMonthValue();
                int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

                for (int day = 1; day <= daysInMonth; day++) {
                    String targetDate = String.format("%04d-%02d-%02d", year, month, day);
                    Long count = getCountForTargetDate(ticketCountList, targetDate);
                    completeCountList.add(new TicketCount(String.valueOf(day), count));
                }
                break;
            }

            case DAILY: {
                int year = localDate.getYear();
                int month = localDate.getMonthValue();
                int day = localDate.getDayOfMonth();

                for (int hour = 0; hour < 24; hour++) {
                    String targetDate = String.format("%04d-%02d-%02d %02d", year, month, day, hour);
                    Long count = getCountForTargetDate(ticketCountList, targetDate);
                    completeCountList.add(new TicketCount(String.valueOf(hour), count));
                }
                break;
            }

            default: {
                throw ApplicationException.from(StatisticsErrorCode.ILLEGAL_STATISTICS_OPTION);
            }
        }

        return completeCountList;
    }

    private Long getCountForTargetDate(List<TicketCount> ticketCountList, String targetDate) {
        return ticketCountList.stream()
            .filter(tc -> tc.targetDate().equals(targetDate))
            .map(TicketCount::count)
            .findFirst()
            .orElse(0L);
    }
}
