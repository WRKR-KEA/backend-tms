package com.wrkr.tickety.domains.ticket.domain.event;

import com.wrkr.tickety.domains.alarm.domain.constant.AgitTicketAlarmMessageType;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import lombok.Builder;

@Builder
public record TicketStatusChangeEvent(
    Ticket ticket,
    Member user,
    AgitTicketAlarmMessageType agitTicketAlarmMessageType
) {

}
