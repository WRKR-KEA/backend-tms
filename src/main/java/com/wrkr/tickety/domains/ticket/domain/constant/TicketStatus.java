package com.wrkr.tickety.domains.ticket.domain.constant;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketStatus {

    REQUEST("요청"),
    IN_PROGRESS("진행"),
    COMPLETE("완료"),
    CANCEL("취소"),
    REJECT("반려"),
    ;

    private final String description;

    public static TicketStatus fromValueOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return Arrays.stream(TicketStatus.values())
            .filter(status -> status.name().equalsIgnoreCase(value.trim()))
            .findFirst()
            .orElse(null);
    }
}
