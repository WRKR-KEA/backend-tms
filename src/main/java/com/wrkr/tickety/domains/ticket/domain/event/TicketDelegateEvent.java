package com.wrkr.tickety.domains.ticket.domain.event;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.AgitTicketNotificationMessageType;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import lombok.Builder;

@Builder
public record TicketDelegateEvent(
    Ticket ticket,
    Member user,
    Member prevManager,
    Member newManager,
    AgitTicketNotificationMessageType agitTicketNotificationMessageType
) {

}
