package com.wrkr.tickety.domains.notification.domain.service.application;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.NotificationType;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitTicketNotificationMessageType;
import com.wrkr.tickety.domains.notification.domain.constant.application.ApplicationNotificationMessageType;
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
            case TICKET_APPROVED -> ApplicationNotificationMessageType.TICKET_APPROVED.format(ticketSerialNumber, ticket.getManager().getNickname());
            case TICKET_REJECT -> ApplicationNotificationMessageType.TICKET_REJECT.format(ticketSerialNumber);
            case TICKET_FINISHED -> ApplicationNotificationMessageType.TICKET_FINISHED.format(ticketSerialNumber);
        };
        sseEmitterService.send(member, NotificationType.TICKET, message);
    }

    @Async
    public void sendCommentApplicationNotification(Member receiver, Ticket ticket) {
        String message = ApplicationNotificationMessageType.COMMENT_UPDATE.format(ticket.getSerialNumber());
        sseEmitterService.send(receiver, NotificationType.COMMENT, message);
    }

    @Async
    public void sendTicketDelegateApplicationNotificationToUser(Member receiver, Member newManager, Ticket ticket) {
        String userMessage = ApplicationNotificationMessageType.TICKET_DELEGATE_MESSAGE_TO_USER.format(
            ticket.getSerialNumber(), newManager.getNickname()
        );
        sseEmitterService.send(receiver, NotificationType.COMMENT, userMessage);
    }

    @Async
    public void sendTicketDelegateApplicationNotificationToManager(Member receiver, Member prevManager, Ticket ticket) {
        String managerMessage = ApplicationNotificationMessageType.TICKET_DELEGATE_MESSAGE_TO_NEW_MANAGER.format(
            ticket.getSerialNumber(), prevManager.getNickname()
        );
        sseEmitterService.send(receiver, NotificationType.COMMENT, managerMessage);
    }

    @Async
    public void sendRemindApplicationNotification(Member member, Ticket ticket) {
        String message = ApplicationNotificationMessageType.REMIND_TICKET.format(ticket.getSerialNumber());
        sseEmitterService.send(member, NotificationType.REMIND, message);
    }
}
