package com.wrkr.tickety.domains.ticket.application.dto.response;

public record CategoryGetAllResponseDTO(
        Long categoryId,
        String name,
        Integer seq,
        Boolean isExistsGuide,
        Boolean isExistsTemplate
) {
}
