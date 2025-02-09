package com.wrkr.tickety.global.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

public record ApplicationPageResponse<T>(

    @Schema(description = "페이지 내용")
    List<T> elements,

    @Schema(description = "현재 페이지 번호", example = "1")
    Integer currentPage,

    @Schema(description = "총 페이지", example = "3")
    Integer totalPages,

    @Schema(description = "총계", example = "50")
    Long totalElements,

    @Schema(description = "페이지 사이즈", example = "20")
    Integer size
) {

    public static <T, E> ApplicationPageResponse<T> of(Page<E> page, Function<E, T> converter) {
        return new ApplicationPageResponse<>(
            page.map(converter).toList(),
            page.getNumber() + 1,
            page.getTotalPages(),
            page.getTotalElements(),
            page.getSize()
        );
    }
}
