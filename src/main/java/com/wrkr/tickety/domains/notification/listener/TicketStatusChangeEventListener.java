package com.wrkr.tickety.domains.notification.listener;

import static com.wrkr.tickety.domains.notification.application.mapper.NotificationMapper.toNotification;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.NotificationType;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitTicketNotificationMessageType;
import com.wrkr.tickety.domains.notification.domain.constant.application.SystemComment;
import com.wrkr.tickety.domains.notification.domain.service.SendAgitNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.SendEmailNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.application.NotificationSaveService;
import com.wrkr.tickety.domains.notification.domain.service.application.SendApplicationNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.kakaowork.KakaoworkNotificationService;
import com.wrkr.tickety.domains.ticket.domain.event.TicketStatusChangeEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.comment.CommentSaveService;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TicketStatusChangeEventListener {

    private final SendAgitNotificationService sendAgitNotificationService;
    private final SendEmailNotificationService sendEmailNotificationService;
    private final SendApplicationNotificationService sendApplicationNotificationService;
    private final KakaoworkNotificationService kakaoworkNotificationService;
    private final CommentSaveService commentSaveService;
    private final NotificationSaveService notificationSaveService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleTicketStatusChangeEvent(TicketStatusChangeEvent ticketStatusChangeEvent) {
        Ticket ticket = ticketStatusChangeEvent.ticket();
        Member member = ticketStatusChangeEvent.user();

        AgitTicketNotificationMessageType agitTicketNotificationMessageType = ticketStatusChangeEvent.agitTicketNotificationMessageType();
        String message = switch (agitTicketNotificationMessageType) {
            case TICKET_APPROVED -> AgitTicketNotificationMessageType.TICKET_APPROVED.format(ticket.getSerialNumber());
            case TICKET_REJECT -> AgitTicketNotificationMessageType.TICKET_REJECT.format(ticket.getSerialNumber());
            case TICKET_FINISHED -> AgitTicketNotificationMessageType.TICKET_FINISHED.format(ticket.getSerialNumber());
        };

        sendAgitNotificationService.sendTicketStatusChangeAgitAlarm(member, ticket, agitTicketNotificationMessageType);

        sendEmailNotificationService.sendTicketStatusChangeEmail(member, ticket, EmailConstants.TICKET_STATUS_CHANGE);

        sendApplicationNotificationService.sendTicketStatusApplicationNotification(member, ticket, agitTicketNotificationMessageType);

        kakaoworkNotificationService.sendTicketStatusChangeKakaoworkAlarm(member, ticket, agitTicketNotificationMessageType);

        notificationSaveService.save(toNotification(member.getMemberId(), member.getProfileImage(), NotificationType.TICKET, message));
    }

    @Async
    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMMIT,
        condition = "#event.ticket.status == T(com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus).IN_PROGRESS"
    )
    public void handleTicketStatusChangeInProgressEvent(TicketStatusChangeEvent event) {
        Ticket ticket = event.ticket();
        Member member = event.ticket().getManager();

        Comment systemComment = Comment.builder()
            .ticket(ticket)
            .member(null)
            .content(SystemComment.TICKET_APPROVE.format(member.getNickname()))
            .build();

        commentSaveService.saveComment(systemComment);
    }
}
