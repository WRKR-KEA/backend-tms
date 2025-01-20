package com.wrkr.tickety.domains.member.application.dto.response;

import lombok.Builder;

@Builder
public record MemberCreateResDTO(
        String memberId
) {
}
