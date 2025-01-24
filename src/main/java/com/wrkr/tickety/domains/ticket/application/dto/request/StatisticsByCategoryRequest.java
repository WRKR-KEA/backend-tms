package com.wrkr.tickety.domains.ticket.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record StatisticsByCategoryRequest(
    @Schema(description = "통계를 확인하고자 하는 날짜", example = "2025-01-12")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date
) {

}
