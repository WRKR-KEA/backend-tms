package com.wrkr.tickety.domains.member.application.dto.request;

import lombok.Builder;

@Builder
public record EmailCreateReqDTO(
        String to,
        String subject,
        String message
) {
}
