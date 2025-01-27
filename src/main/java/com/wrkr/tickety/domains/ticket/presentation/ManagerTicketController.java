package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.request.StatisticsByCategoryRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByCategoryUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Manager Ticket Controller")
@RequestMapping("/api/manager")
public class ManagerTicketController {

    private final StatisticsByCategoryUseCase statisticsByCategoryUseCase;

    @PostMapping("/statistics/{type}")
    @Operation(summary = "카테고리별 통계 조회")
    public ApplicationResponse<StatisticsByCategoryResponse> getStatistics(
        @Parameter(description = "통계 타입", example = "daily", required = true)
        @PathVariable String type,
        @Parameter(description = "통계를 확인하고자 하는 날짜", example = "2025-01-12", required = true)
        @RequestBody @Valid StatisticsByCategoryRequest request
    ) {

        StatisticsType statisticsType = StatisticsType.from(type);
        return ApplicationResponse.onSuccess(statisticsByCategoryUseCase.getStatisticsByCategory(statisticsType, request.date()));
    }
}
