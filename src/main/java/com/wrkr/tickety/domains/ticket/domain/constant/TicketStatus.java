package com.wrkr.tickety.domains.ticket.domain.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketStatus {

    REQUEST("요청", 1),
    CANCEL("취소", 2),
    IN_PROGRESS("진행", 3),
    REJECT("반려", 4),
    COMPLETE("완료", 5),
    ;

    private final String description;
    private final int seq;

    @JsonCreator
    public static TicketStatus from(String status) {
        return Arrays.stream(TicketStatus.values())
            .filter(ticketStatus -> ticketStatus.name().equalsIgnoreCase(status))
            .findFirst()
            .orElse(null);
    }
}
