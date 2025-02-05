package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.domains.ticket.exception.StatisticsErrorCode.ILLEGAL_STATISTICS_OPTION;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.METHOD_ARGUMENT_NOT_VALID;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.StatisticsByCategoryRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByCategoryUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsGetUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Ticket Statistics Controller")
@RequestMapping("/api/manager/statistics")
public class TicketStatisticsController {

    private final StatisticsByCategoryUseCase statisticsByCategoryUseCase;
    private final StatisticsGetUseCase statisticsGetUseCase;

    @PostMapping("/{statisticsType}")
    @Operation(summary = "카테고리별 통계 조회")
    public ApplicationResponse<StatisticsByCategoryResponse> getStatistics(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "통계 타입", example = "daily", required = true) @PathVariable StatisticsType statisticsType,
        @Parameter(description = "통계를 확인하고자 하는 날짜", example = "2025-01-12", required = true) @RequestBody @Valid StatisticsByCategoryRequest request
    ) {
        return ApplicationResponse.onSuccess(statisticsByCategoryUseCase.getStatisticsByCategory(statisticsType, request.date()));
    }

    @Operation(summary = "기간별 & 티켓 상태별 티켓 개수 조회", description = "기간별 & 티켓 상태별로 티켓의 개수를 조회합니다.")
    @Parameters({
        @Parameter(name = "type", description = "통계 타입 (DAILY | MONTHLY | YEARLY | TOTAL)", example = "DAILY"),
        @Parameter(name = "status", description = "티켓 상태 (REQUEST | IN_PROGRESS | COMPLETE | CANCEL | REJECT)", example = "IN_PROGRESS"),
    })
    @CustomErrorCodes(
        commonErrorCodes = {METHOD_ARGUMENT_NOT_VALID},
        statisticsErrorCodes = {ILLEGAL_STATISTICS_OPTION}
    )
    @GetMapping("/count")
    public ApplicationResponse<StatisticsByTicketStatusResponse> getTicketCountStatistics(
        @AuthenticationPrincipal Member member,
        @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().toString()}") String date,
        @RequestParam(defaultValue = "TOTAL") StatisticsType type,
        @RequestParam(required = false) TicketStatus status
    ) {
        return ApplicationResponse.onSuccess(
            statisticsGetUseCase.getTicketCountStatistics(date, type, status)
        );
    }
}
