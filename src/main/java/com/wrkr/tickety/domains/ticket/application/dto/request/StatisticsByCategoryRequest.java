package com.wrkr.tickety.domains.ticket.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record StatisticsByCategoryRequest(
    @Schema(description = "통계를 확인하고자 하는 날짜", example = "yyyy-mm-dd")
    @Pattern(regexp = "^(\\d{4}(-\\d{2}(-\\d{2})?)?)$", message = "잘못된 날짜 형식입니다.")
    String date
) {

}
