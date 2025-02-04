package com.wrkr.tickety.domains.alarm.listener;

import com.wrkr.tickety.domains.alarm.domain.service.SendAgitNotificationService;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.event.TicketDelegateEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketDelegateListener {

    private final SendAgitNotificationService sendAgitNotificationService;

    @Async
    @EventListener
    public void handleTicketDelegateEvent(TicketDelegateEvent ticketDelegateEvent) {
        Member prevManager = ticketDelegateEvent.prevManager();
        Member newManager = ticketDelegateEvent.newManager();
        Ticket ticket = ticketDelegateEvent.ticket();

        sendAgitNotificationService.sendTicketDelegateAgitAlarm(prevManager, newManager, ticket);
    }

}
