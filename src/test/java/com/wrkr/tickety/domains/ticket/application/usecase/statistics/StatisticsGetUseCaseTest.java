package com.wrkr.tickety.domains.ticket.application.usecase.statistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse.TicketCount;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.global.utils.date.DateUtil;
import com.wrkr.tickety.global.utils.date.TimePeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("기간별 & 상태별 티켓 개수 조회 UseCase Layer Test")
public class StatisticsGetUseCaseTest {

    @InjectMocks
    private StatisticsGetUseCase statisticsGetUseCase;

    @Mock
    private TicketHistoryGetService ticketHistoryGetService;

    @DisplayName("통계 타입이 MONTHLY일 때 월간 티켓 개수 조회에 성공한다.")
    @Test
    void getMonthlyTicketCountStatisticsTest() {
        // given
        String date = "2025-01";
        StatisticsType type = StatisticsType.MONTHLY;
        TicketStatus status = TicketStatus.REQUEST;
        LocalDate localDate = DateUtil.convertToLocalDate(date + "-01");
        TimePeriod timePeriod = DateUtil.extractTimePeriod(localDate.atStartOfDay(), type);

        TicketCount ticketCount1 = new TicketCount("2025-01-01", 1L);
        TicketCount ticketCount2 = new TicketCount("2025-01-02", 1L);
        List<TicketCount> ticketCounts = List.of(ticketCount1, ticketCount2);

        given(ticketHistoryGetService.getTicketCountStatistics(
            any(LocalDateTime.class),
            any(LocalDateTime.class),
            any(StatisticsType.class),
            any(TicketStatus.class)
        )).willReturn(ticketCounts);

        // when
        StatisticsByTicketStatusResponse response = statisticsGetUseCase.getTicketCountStatistics(date, type, status);

        // then
        assertThat(response.baseDate()).isEqualTo("2025-01");
        assertThat(response.countList()).isNotNull().hasSize(31);
        assertThat(response.countList().get(0).targetDate()).isEqualTo("1");
        assertThat(response.countList().get(10).targetDate()).isEqualTo("11");
        verify(ticketHistoryGetService).getTicketCountStatistics(
            eq(timePeriod.getStartDateTime()),
            eq(timePeriod.getEndDateTime()),
            eq(type),
            eq(status)
        );
    }

    @DisplayName("통계 타입이 DAILY일 때 일간 티켓 개수 조회에 성공한다.")
    @Test
    void getDailyTicketCountStatisticsTest() {
        // given
        String date = "2025-01-01";
        StatisticsType type = StatisticsType.DAILY;
        TicketStatus status = TicketStatus.REQUEST;
        LocalDate localDate = DateUtil.convertToLocalDate(date);
        TimePeriod timePeriod = DateUtil.extractTimePeriod(localDate.atStartOfDay(), type);

        TicketCount ticketCount = new TicketCount("2025-01-01 10", 1L);
        List<TicketCount> ticketCounts = List.of(ticketCount);

        given(ticketHistoryGetService.getTicketCountStatistics(
            any(LocalDateTime.class),
            any(LocalDateTime.class),
            any(StatisticsType.class),
            any(TicketStatus.class)
        )).willReturn(ticketCounts);

        // when
        StatisticsByTicketStatusResponse response = statisticsGetUseCase.getTicketCountStatistics(date, type, status);

        // then
        assertThat(response.baseDate()).isEqualTo("2025-01-01");
        assertThat(response.countList()).isNotNull().hasSize(24);
        assertThat(response.countList().get(0).targetDate()).isEqualTo("0");
        assertThat(response.countList().get(10).targetDate()).isEqualTo("10");
        assertThat(response.countList().get(10).count()).isEqualTo(1L);
        verify(ticketHistoryGetService).getTicketCountStatistics(
            eq(timePeriod.getStartDateTime()),
            eq(timePeriod.getEndDateTime()),
            eq(type),
            eq(status)
        );
    }
}
