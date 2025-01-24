package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.request.StatisticsByCategoryRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByCategoryUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.global.response.ApplicationResponse;
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
    public ApplicationResponse<StatisticsByCategoryResponse> getStatistics(
        @PathVariable StatisticsType type,
        @RequestBody @Valid StatisticsByCategoryRequest request
    ) {

        return ApplicationResponse.onSuccess(statisticsByCategoryUseCase.getStatisticsByCategory(type, request.date()));
    }
}
