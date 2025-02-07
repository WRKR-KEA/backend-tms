package com.wrkr.tickety.domains.notification.listener;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.service.SendAgitNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.SendApplicationNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.SendEmailNotificationService;
import com.wrkr.tickety.domains.ticket.domain.event.CommentCreateEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@EnableAsync
public class CommentCreateEventListener {

    private final SendAgitNotificationService sendAgitNotificationService;
    private final SendEmailNotificationService sendEmailNotificationService;
    private final SendApplicationNotificationService sendApplicationNotificationService;

    @Async
    @EventListener
    public void handleCommentCreateEvent(CommentCreateEvent commentCreateEvent) {
        Comment comment = commentCreateEvent.comment();
        Member member = comment.getMember();

        Ticket ticket = comment.getTicket();
        Member receiver;

        if (member.getRole().equals(Role.USER)) {
            receiver = ticket.getManager();
        } else {
            receiver = ticket.getUser();
        }
        sendAgitNotificationService.sendCommentCreateAgitAlarm(receiver, ticket);
        EmailCreateRequest emailCreateRequest = EmailCreateRequest.builder()
            .to(receiver.getEmail())
            .subject(EmailConstants.TICKET_COMMENT_SUBJECT)
            .build();
        sendEmailNotificationService.sendCommentCreateEmail(emailCreateRequest, ticket, EmailConstants.TICKET_COMMENT);
        sendApplicationNotificationService.sendCommentApplicationNotification(receiver, ticket);
    }
}
