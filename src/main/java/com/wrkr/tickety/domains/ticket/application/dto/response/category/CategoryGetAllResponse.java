package com.wrkr.tickety.domains.ticket.application.dto.response.category;

public record CategoryGetAllResponse(
        Long categoryId,
        String name,
        Integer seq,
        Boolean isExistsGuide,
        Boolean isExistsTemplate
) {
}
