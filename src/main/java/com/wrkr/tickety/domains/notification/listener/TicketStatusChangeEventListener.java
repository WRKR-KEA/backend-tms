package com.wrkr.tickety.domains.notification.listener;

import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitTicketNotificationMessageType;
import com.wrkr.tickety.domains.notification.domain.constant.systemComment.SystemComment;
import com.wrkr.tickety.domains.notification.domain.service.SendAgitNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.SendEmailNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.application.SendApplicationNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.kakaowork.KakaoworkMessageService;
import com.wrkr.tickety.domains.ticket.domain.event.TicketStatusChangeEvent;
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
public class TicketStatusChangeEventListener {

    private final SendAgitNotificationService sendAgitNotificationService;
    private final SendEmailNotificationService sendEmailNotificationService;
    private final SendApplicationNotificationService sendApplicationNotificationService;
    private final KakaoworkMessageService kakaoworkMessageService;
    private final CommentSaveService commentSaveService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
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

    @Async
    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMMIT,
        condition = "#event.ticket.status == T(com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus).IN_PROGRESS"
    )
    public void handleTicketStatusChangeInProgressEvent(TicketStatusChangeEvent event) {
        Ticket ticket = event.ticket();
        Member member = event.user();

        Comment systemComment = Comment.builder()
            .ticket(ticket)
            .member(null)
            .content(SystemComment.TICKET_APPROVE.format(member.getNickname()))
            .build();

        commentSaveService.saveComment(systemComment);
    }
}
