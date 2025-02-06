package com.wrkr.tickety.infrastructure.email;

import lombok.Builder;

@Builder
public record EmailCreateRequest(
    String to,
    String subject,
    String message
) {

}
