package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.domains.ticket.exception.StatisticsErrorCode.ILLEGAL_STATISTICS_OPTION;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByStatusResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByStatusUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Ticket Statistics Controller")
@RequestMapping("/api/manager/statistics")
public class TicketStatisticsController {

    private final StatisticsByStatusUseCase statisticsByStatusUseCase;

    @GetMapping("/{statisticsType}/status")
    @Operation(summary = "일간/월간/연간 상태별 요약 조회")
    @CustomErrorCodes(statisticsErrorCodes = {ILLEGAL_STATISTICS_OPTION})
    public ApplicationResponse<StatisticsByStatusResponse> getStatisticsByStatusDaily(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "통계 타입 (DAILY | MONTHLY | YEARLY)", example = "daily", required = true) @PathVariable String statisticsType,
        @Parameter(description = "통계를 확인하고자 하는 기준일", example = "2025-01-01") @RequestParam(required = false) String date
    ) {
        return ApplicationResponse.onSuccess(statisticsByStatusUseCase.getStatisticsByStatus(StatisticsType.from(statisticsType), date));
    }
}