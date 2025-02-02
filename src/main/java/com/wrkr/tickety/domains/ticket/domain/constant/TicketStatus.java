package com.wrkr.tickety.domains.ticket.domain.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public static TicketStatus from(String status) {
        return Arrays.stream(TicketStatus.values())
            .filter(ticketStatus -> ticketStatus.name().equalsIgnoreCase(status))
            .findFirst()
            .orElse(null);
    }
}
