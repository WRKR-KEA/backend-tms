package com.wrkr.tickety.domains.alarm.listener;

import com.wrkr.tickety.domains.alarm.domain.service.SendAgitAlarmService;
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

    private final SendAgitAlarmService sendAgitAlarmService;

    @Async
    @EventListener
    public void handleTicketDelegateEvent(TicketDelegateEvent ticketDelegateEvent) {
        Member prevManager = ticketDelegateEvent.prevManager();
        Member newManager = ticketDelegateEvent.newManager();
        Ticket ticket = ticketDelegateEvent.ticket();

        sendAgitAlarmService.sendTicketDelegateAgitAlarm(prevManager, newManager, ticket);
    }

}
