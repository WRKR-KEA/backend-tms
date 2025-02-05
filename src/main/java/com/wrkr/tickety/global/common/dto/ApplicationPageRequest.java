package com.wrkr.tickety.global.common.dto;

import com.wrkr.tickety.domains.ticket.domain.constant.SortType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record ApplicationPageRequest(
    @Schema(description = "페이지 번호, 1 이상이어야 합니다.", defaultValue = "1", example = "1")
    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    Integer page,

    @Schema(description = "페이지 크기, 10 이상이어야 합니다.", defaultValue = "20", example = "20")
    Integer size,

    @Schema(description = "정렬 타입 (NEWEST | OLDEST | UPDATED)", defaultValue = "UPDATED", example = "NEWEST")
    SortType sortType
) {

    public ApplicationPageRequest(Integer page, Integer size, SortType sortType) {
        this.page = page == null ? 0 : Math.max(0, page - 1);
        this.size = size == null ? 20 : Math.max(10, size);
        this.sortType = sortType;
    }

    public Pageable toPageable() {

        Sort sort = switch (this.sortType) {
            case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case OLDEST -> Sort.by(Sort.Direction.ASC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "updatedAt");
        };

        return PageRequest.of(page, size, sort);
    }

    public Pageable toPageableNoSort() {
        return PageRequest.of(page, size);
    }
}
