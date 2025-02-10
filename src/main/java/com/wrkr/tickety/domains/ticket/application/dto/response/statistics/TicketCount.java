package com.wrkr.tickety.domains.ticket.application.dto.response.statistics;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.IntStream;
import lombok.Builder;

@Builder
public record TicketCount(
    @Schema(description = "카테고리 id", example = "Bqs3C822lkMNdWlmE-szUw") String categoryId,
    @Schema(description = "카테고리 이름", example = "개발") String categoryName,
    @Schema(description = "티켓 수", example = "10") long count) {

    public static List<TicketCount> from(List<Long> ticketCountByCategoryList, List<Category> categoryList) {
        return IntStream.range(0, ticketCountByCategoryList.size()).mapToObj(
            i -> TicketCount.builder().categoryId(PkCrypto.encrypt(categoryList.get(i).getCategoryId())).categoryName(categoryList.get(i).getName())
                .count(ticketCountByCategoryList.get(i)).build()).toList();
    }
}