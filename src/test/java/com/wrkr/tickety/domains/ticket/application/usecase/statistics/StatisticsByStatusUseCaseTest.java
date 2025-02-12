package com.wrkr.tickety.domains.ticket.application.usecase.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByStatusResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.global.utils.date.DateUtil;
import com.wrkr.tickety.global.utils.date.TimePeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatisticsByStatusUseCaseTest {

    @InjectMocks
    StatisticsByStatusUseCase statisticsByStatusUseCase;
    @Mock
    TicketHistoryGetService ticketHistoryGetService;
    @Mock
    TicketGetService ticketGetService;


    @Test
    @DisplayName("일간 상태별 통계 조회")
    void getStatisticsByStatusInDaily() {
        //given
        StatisticsType type = StatisticsType.DAILY;
        String date = LocalDate.now().toString();
        LocalDateTime localDateTime = DateUtil.convertToLocalDate(date).atStartOfDay();
        TimePeriod timePeriod = DateUtil.extractTimePeriod(localDateTime, type);
        StatisticsByStatusResponse expected = StatisticsByStatusResponse.builder()
            .date(date)
            .request(3L)
            .accept(1L)
            .reject(1L)
            .complete(1L)
            .build();

        //when
        when(ticketGetService.countTicketsCreatedBetween(timePeriod.getStartDateTime(), timePeriod.getEndDateTime())).thenReturn(3L);
        when(ticketHistoryGetService.getStatisticsByStatus(TicketStatus.IN_PROGRESS, timePeriod.getStartDateTime(), timePeriod.getEndDateTime())).thenReturn(1L);
        when(ticketHistoryGetService.getStatisticsByStatus(TicketStatus.REJECT, timePeriod.getStartDateTime(), timePeriod.getEndDateTime())).thenReturn(1L);
        when(ticketHistoryGetService.getStatisticsByStatus(TicketStatus.COMPLETE, timePeriod.getStartDateTime(), timePeriod.getEndDateTime())).thenReturn(1L);

        StatisticsByStatusResponse statisticsByStatusResponse = statisticsByStatusUseCase.getStatisticsByStatus(type, date);

        //then
        assertNotNull(statisticsByStatusResponse, "통계 조회 결과는 null이 아니어야 합니다.");
        assertEquals(expected.date(), statisticsByStatusResponse.date(), "요청 날짜가 일치해야 합니다.");
        assertEquals(expected.accept(), statisticsByStatusResponse.accept(), "accept 통계가 일치해야 합니다.");
        assertEquals(expected.reject(), statisticsByStatusResponse.reject(), "reject 통계가 일치해야 합니다.");
        assertEquals(expected.complete(), statisticsByStatusResponse.complete(), "complete 통계가 일치해야 합니다.");
        assertEquals(expected.request(), statisticsByStatusResponse.request(), "request 통계가 일치해야 합니다.");
    }

    @Test
    @DisplayName("월간 상태별 통계 조회")
    void getStatisticsByStatusInMonthly() {
        //given
        StatisticsType type = StatisticsType.DAILY;
        String date = LocalDate.now().toString();
        LocalDateTime localDateTime = DateUtil.convertToLocalDate(date).atStartOfDay();
        TimePeriod timePeriod = DateUtil.extractTimePeriod(localDateTime, type);
        StatisticsByStatusResponse expected = StatisticsByStatusResponse.builder()
            .date(date)
            .request(3L)
            .accept(1L)
            .reject(1L)
            .complete(1L)
            .build();

        //when
        when(ticketGetService.countTicketsCreatedBetween(timePeriod.getStartDateTime(), timePeriod.getEndDateTime())).thenReturn(3L);
        when(ticketHistoryGetService.getStatisticsByStatus(TicketStatus.IN_PROGRESS, timePeriod.getStartDateTime(), timePeriod.getEndDateTime())).thenReturn(1L);
        when(ticketHistoryGetService.getStatisticsByStatus(TicketStatus.REJECT, timePeriod.getStartDateTime(), timePeriod.getEndDateTime())).thenReturn(1L);
        when(ticketHistoryGetService.getStatisticsByStatus(TicketStatus.COMPLETE, timePeriod.getStartDateTime(), timePeriod.getEndDateTime())).thenReturn(1L);

        StatisticsByStatusResponse statisticsByStatusResponse = statisticsByStatusUseCase.getStatisticsByStatus(type, date);

        //then
        assertNotNull(statisticsByStatusResponse, "통계 조회 결과는 null이 아니어야 합니다.");
        assertEquals(expected.date(), statisticsByStatusResponse.date(), "요청 날짜가 일치해야 합니다.");
        assertEquals(expected.accept(), statisticsByStatusResponse.accept(), "accept 통계가 일치해야 합니다.");
        assertEquals(expected.reject(), statisticsByStatusResponse.reject(), "reject 통계가 일치해야 합니다.");
        assertEquals(expected.complete(), statisticsByStatusResponse.complete(), "complete 통계가 일치해야 합니다.");
        assertEquals(expected.request(), statisticsByStatusResponse.request(), "request 통계가 일치해야 합니다.");
    }
}