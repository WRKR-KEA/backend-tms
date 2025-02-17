package com.wrkr.tickety.domains.notification.domain.service.application;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.NotificationType;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitCommentNotificationMessage;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitTicketDelegateNotificationMessage;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitTicketNotificationMessageType;
import com.wrkr.tickety.domains.notification.domain.constant.application.Remind;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SendApplicationNotificationService {

    private final SseEmitterService sseEmitterService;

    @Async
    public void sendTicketStatusApplicationNotification(Member member, Ticket ticket, AgitTicketNotificationMessageType agitTicketNotificationMessageType) {
        String ticketSerialNumber = ticket.getSerialNumber();
        String message = switch (agitTicketNotificationMessageType) {
            case TICKET_APPROVED -> AgitTicketNotificationMessageType.TICKET_APPROVED.format(ticketSerialNumber);
            case TICKET_REJECT -> AgitTicketNotificationMessageType.TICKET_REJECT.format(ticketSerialNumber);
            case TICKET_FINISHED -> AgitTicketNotificationMessageType.TICKET_FINISHED.format(ticketSerialNumber);
        };
        sseEmitterService.send(member, NotificationType.TICKET, message);
    }

    @Async
    public void sendCommentApplicationNotification(Member receiver, Ticket ticket) {
        String message = AgitCommentNotificationMessage.COMMENT_UPDATE.format(ticket.getSerialNumber());
        sseEmitterService.send(receiver, NotificationType.COMMENT, message);
    }

    @Async
    public void sendTicketDelegateApplicationNotification(Member prevManager, Member newManager, Ticket ticket) {
        String userMessage = AgitTicketDelegateNotificationMessage.TICKET_DELEGATE_MESSAGE_TO_USER.format(
            ticket.getSerialNumber(), newManager.getNickname()
        );
        sseEmitterService.send(ticket.getUser(), NotificationType.COMMENT, userMessage);

        String managerMessage = AgitTicketDelegateNotificationMessage.TICKET_DELEGATE_MESSAGE_TO_NEW_MANAGER.format(
            prevManager.getNickname(), ticket.getSerialNumber()
        );
        sseEmitterService.send(newManager, NotificationType.COMMENT, managerMessage);
    }

    @Async
    public void sendRemindApplicationNotification(Member member, Ticket ticket) {
        String message = Remind.REMIND_TICKET.format(ticket.getSerialNumber());
        sseEmitterService.send(member, NotificationType.REMIND, message);
    }
}
