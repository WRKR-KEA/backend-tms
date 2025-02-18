package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.event.RemindEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
public class TicketReminderUseCase {

    private static final String REMIND_KEY_PREFIX = "remind:";
    private static final Duration REMIND_COOLDOWN = Duration.ofHours(24);

    private final RedisTemplate<String, String> redisTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TicketGetService ticketGetService;

    public boolean sendReminder(Member sender, Long receiverId, Long ticketId) {
        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId);
        Member receiver;
        if (sender.getRole().equals(Role.USER)) {
            receiver = ticket.getManager();
        } else {
            receiver = ticket.getUser();
        }

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String reminderKey = REMIND_KEY_PREFIX + ticket.getSerialNumber() + ":" + sender.getMemberId() + ":" + receiver.getMemberId() + ":" + today;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(reminderKey))) {
            return false;
        }

        sendNotification(receiver, ticket);
        redisTemplate.opsForValue().set(reminderKey, "sent", REMIND_COOLDOWN);
        return true;
    }

    private void sendNotification(Member member, Ticket ticket) {
        applicationEventPublisher.publishEvent(RemindEvent.builder()
            .ticket(ticket)
            .member(member)
            .build());
    }
}
