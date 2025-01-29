package com.wrkr.tickety.domains.ticket.application.dto.response;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

public record PageResponse<T>(

    List<T> elements,
    Integer currentPage,
    Integer totalPages,
    Long totalElements,
    Integer size
) {

    public static <T, E> PageResponse<T> of(Page<E> page, Function<E, T> converter) {
        return new PageResponse<>(
            page.map(converter).toList(),
            page.getNumber() + 1,
            page.getTotalPages(),
            page.getTotalElements(),
            page.getSize()
        );
    }
}
