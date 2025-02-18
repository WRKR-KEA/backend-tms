package com.wrkr.tickety.domains.notification.listener;

import static com.wrkr.tickety.domains.notification.application.mapper.NotificationMapper.toNotification;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.NotificationType;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitCommentNotificationMessage;
import com.wrkr.tickety.domains.notification.domain.service.SendAgitNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.SendEmailNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.application.NotificationSaveService;
import com.wrkr.tickety.domains.notification.domain.service.application.SendApplicationNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.kakaowork.KakaoworkNotificationService;
import com.wrkr.tickety.domains.ticket.domain.event.CommentCreateEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class CommentCreateEventListener {

    private final SendAgitNotificationService sendAgitNotificationService;
    private final SendEmailNotificationService sendEmailNotificationService;
    private final SendApplicationNotificationService sendApplicationNotificationService;
    private final KakaoworkNotificationService kakaoworkNotificationService;
    private final NotificationSaveService notificationSaveService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCreateEvent(CommentCreateEvent commentCreateEvent) {
        Comment comment = commentCreateEvent.comment();
        Member member = comment.getMember();
        Ticket ticket = comment.getTicket();
        String message = AgitCommentNotificationMessage.COMMENT_UPDATE.format(ticket.getSerialNumber());
        Member receiver;

        if (member.getRole().equals(Role.USER)) {
            receiver = ticket.getManager();
        } else {
            receiver = ticket.getUser();
        }

        sendAgitNotificationService.sendCommentCreateAgitAlarm(receiver, ticket);

        sendEmailNotificationService.sendCommentCreateEmail(receiver, ticket, EmailConstants.TICKET_COMMENT);

        sendApplicationNotificationService.sendCommentApplicationNotification(receiver, ticket);

        kakaoworkNotificationService.sendCommentCreateKakaoworkAlarm(receiver, ticket);

        notificationSaveService.save(toNotification(receiver.getMemberId(), member.getProfileImage(), NotificationType.COMMENT, message));
    }
}
