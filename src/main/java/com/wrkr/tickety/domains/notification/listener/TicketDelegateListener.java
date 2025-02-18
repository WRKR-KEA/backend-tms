package com.wrkr.tickety.domains.notification.listener;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.application.SystemComment;
import com.wrkr.tickety.domains.notification.domain.service.SendAgitNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.SendEmailNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.application.SendApplicationNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.kakaowork.KakaoworkNotificationService;
import com.wrkr.tickety.domains.ticket.domain.event.TicketDelegateEvent;
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
public class TicketDelegateListener {

    private final SendAgitNotificationService sendAgitNotificationService;
    private final SendEmailNotificationService sendEmailNotificationService;
    private final SendApplicationNotificationService sendApplicationNotificationService;
    private final KakaoworkNotificationService kakaoworkNotificationService;
    private final CommentSaveService commentSaveService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTicketDelegateEvent(TicketDelegateEvent ticketDelegateEvent) {
        Member prevManager = ticketDelegateEvent.prevManager();
        Member newManager = ticketDelegateEvent.newManager();
        Ticket ticket = ticketDelegateEvent.ticket();
        Member user = ticket.getUser();

        sendAgitNotificationService.sendTicketDelegateAgitAlarmToUser(user, newManager, ticket);
        sendAgitNotificationService.sendTicketDelegateAgitAlarmToManager(newManager, prevManager, ticket);

        sendEmailNotificationService.sendDelegateTicketManagerEmailToUser(user, ticket, newManager, EmailConstants.TICKET_DELEGATE_TO_USER);
        sendEmailNotificationService.sendDelegateTicketManagerEmailToNewManager(newManager, ticket, prevManager, EmailConstants.TICKET_DELEGATE_TO_NEW_MANAGER);

        sendApplicationNotificationService.sendTicketDelegateApplicationNotificationToUser(user, newManager, ticket);
        sendApplicationNotificationService.sendTicketDelegateApplicationNotificationToManager(newManager, prevManager, ticket);

        kakaoworkNotificationService.sendTicketDelegateKakaoworkAlarmToUser(user, newManager, ticket);
        kakaoworkNotificationService.sendTicketDelegateKakaoworkAlarmToManager(newManager, prevManager, ticket);

        Comment systemComment = Comment.builder()
            .ticket(ticket)
            .content(SystemComment.TICKET_DELEGATE.format(newManager.getNickname()))
            .member(null).build();

        commentSaveService.saveComment(systemComment);
    }
}
