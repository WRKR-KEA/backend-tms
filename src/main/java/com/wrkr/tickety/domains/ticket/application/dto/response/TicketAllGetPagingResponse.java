package com.wrkr.tickety.domains.ticket.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record TicketAllGetPagingResponse(
        @Schema(description = "티켓 정보 목록")
        List<TicketAllGetResponse> tickets,

        @Schema(description = "현재 페이지", example = "1")
        int currentPage,

        @Schema(description = "총 페이지 수", example = "10")
        int totalPages,

        @Schema(description = "총 항목 수", example = "100")
        long totalElements,

        @Schema(description = "페이지 크기", example = "10")
        int size
) {
    public static TicketAllGetPagingResponse from(Page<TicketAllGetResponse> page) {
        return new TicketAllGetPagingResponse(
                page.getContent(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize()
        );
    }
}
