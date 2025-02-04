package com.wrkr.tickety.domains.alarm.listener;

import com.wrkr.tickety.domains.alarm.domain.constant.AgitTicketAlarmMessageType;
import com.wrkr.tickety.domains.alarm.domain.service.SendAgitAlarmService;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.event.TicketStatusChangeEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketStatusChangeEventListener {

    private final SendAgitAlarmService sendAgitAlarmService;

    @Async
    @EventListener
    public void handleTicketStatusChangeEvent(TicketStatusChangeEvent ticketStatusChangeEvent) {
        Ticket ticket = ticketStatusChangeEvent.ticket();
        Member member = ticketStatusChangeEvent.user();
        AgitTicketAlarmMessageType agitTicketAlarmMessageType = ticketStatusChangeEvent.agitTicketAlarmMessageType();
        sendAgitAlarmService.sendTicketStatusChangeAgitAlarm(member, ticket, agitTicketAlarmMessageType);
    }
}
