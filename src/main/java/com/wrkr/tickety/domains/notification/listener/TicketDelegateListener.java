package com.wrkr.tickety.domains.notification.listener;

import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.systemComment.SystemComment;
import com.wrkr.tickety.domains.notification.domain.service.SendAgitNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.SendEmailNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.application.SendApplicationNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.kakaowork.KakaoworkMessageService;
import com.wrkr.tickety.domains.ticket.domain.event.TicketDelegateEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.comment.CommentSaveService;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@EnableAsync
public class TicketDelegateListener {

    private final SendAgitNotificationService sendAgitNotificationService;
    private final SendEmailNotificationService sendEmailNotificationService;
    private final SendApplicationNotificationService sendApplicationNotificationService;
    private final KakaoworkMessageService kakaoworkMessageService;
    private final CommentSaveService commentSaveService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTicketDelegateEvent(TicketDelegateEvent ticketDelegateEvent) {
        Member prevManager = ticketDelegateEvent.prevManager();
        Member newManager = ticketDelegateEvent.newManager();
        Ticket ticket = ticketDelegateEvent.ticket();

        sendAgitNotificationService.sendTicketDelegateAgitAlarm(prevManager, newManager, ticket);

        EmailCreateRequest emailCreateRequestToUser = EmailMapper.toEmailCreateRequest(ticket.getUser().getEmail(), EmailConstants.TICKET_DELEGATE_SUBJECT,
                                                                                       null);

        sendEmailNotificationService.sendDelegateTicketManagerEmailToUser(emailCreateRequestToUser, ticket, newManager, EmailConstants.TICKET_DELEGATE_TO_USER);

        EmailCreateRequest emailCreateRequestToNewManager = EmailMapper.toEmailCreateRequest(newManager.getEmail(), EmailConstants.TICKET_DELEGATE_SUBJECT,
                                                                                             null);

        sendEmailNotificationService.sendDelegateTicketManagerEmailToNewManager(emailCreateRequestToNewManager, ticket, prevManager,
                                                                                EmailConstants.TICKET_DELEGATE_TO_NEW_MANAGER);

        sendApplicationNotificationService.sendTicketDelegateApplicationNotification(prevManager, newManager, ticket);
        kakaoworkMessageService.sendTicketDelegateKakaoworkAlarm(prevManager, newManager, ticket);

        Comment systemComment = Comment.builder()
            .ticket(ticket)
            .content(SystemComment.TICKET_DELEGATE.format(newManager.getNickname()))
            .member(null).build();

        commentSaveService.saveComment(systemComment);
    }
}
