package com.wrkr.tickety.domains.alarm.listener;

import com.wrkr.tickety.domains.alarm.domain.service.SendAgitNotificationService;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.event.CommentCreateEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentCreateEventListener {

    private final SendAgitNotificationService sendAgitNotificationService;

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
    }
}
