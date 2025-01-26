package com.wrkr.tickety.domains.ticket.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record ManagerTicketAllGetPagingResponse(
    @Schema(description = "티켓 정보 목록")
    List<ManagerTicketAllGetResponse> tickets,

    @Schema(description = "현재 페이지", example = "1")
    int currentPage,

    @Schema(description = "총 페이지 수", example = "10")
    int totalPages,

    @Schema(description = "총 항목 수", example = "100")
    long totalElements,

    @Schema(description = "페이지 크기", example = "10")
    int size
) {

    public static ManagerTicketAllGetPagingResponse from(Page<ManagerTicketAllGetResponse> page) {

        return ManagerTicketAllGetPagingResponse.builder()
            .tickets(page.getContent())
            .currentPage(page.getNumber() + 1)
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
