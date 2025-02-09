package com.wrkr.tickety.domains.notification.listener;

import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitTicketNotificationMessageType;
import com.wrkr.tickety.domains.notification.domain.service.SendAgitNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.SendEmailNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.application.SendApplicationNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.kakaowork.KakaoworkMessageService;
import com.wrkr.tickety.domains.ticket.domain.event.TicketStatusChangeEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableAsync
public class TicketStatusChangeEventListener {

    private final SendAgitNotificationService sendAgitNotificationService;
    private final SendEmailNotificationService sendEmailNotificationService;
    private final SendApplicationNotificationService sendApplicationNotificationService;
    private final KakaoworkMessageService kakaoworkMessageService;

    @EventListener
    @Async
    public void handleTicketStatusChangeEvent(TicketStatusChangeEvent ticketStatusChangeEvent) {
        Ticket ticket = ticketStatusChangeEvent.ticket();
        Member member = ticketStatusChangeEvent.user();
        AgitTicketNotificationMessageType agitTicketNotificationMessageType = ticketStatusChangeEvent.agitTicketNotificationMessageType();
        sendAgitNotificationService.sendTicketStatusChangeAgitAlarm(member, ticket, agitTicketNotificationMessageType);
        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(member.getEmail(), EmailConstants.TICKET_STATUS_CHANGE_SUBJECT, null);
        sendEmailNotificationService.sendTicketStatusChangeEmail(emailCreateRequest, ticket, EmailConstants.TICKET_STATUS_CHANGE);
        sendApplicationNotificationService.sendTicketStatusApplicationNotification(member, ticket, agitTicketNotificationMessageType);
        kakaoworkMessageService.sendTicketStatusChangeKakaoworkAlarm(member, ticket, agitTicketNotificationMessageType);
    }
}
