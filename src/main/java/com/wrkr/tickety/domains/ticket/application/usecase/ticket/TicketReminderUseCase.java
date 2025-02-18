package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static com.wrkr.tickety.domains.notification.exception.NotificationErrorCode.RECEIVER_NOT_FOUND;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.domain.event.RemindEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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
    private final MemberGetService memberGetService;
    private final TicketGetService ticketGetService;

    public boolean sendReminder(Long senderId, Long receiverId, Long ticketId) {
        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId);
        Optional<Member> receiver = memberGetService.findMemberByMemberIdAndIsDeleted(receiverId, false);
        if (receiver.isEmpty()) {
            throw ApplicationException.from(RECEIVER_NOT_FOUND);
        }
        Member member = receiver.get();

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String reminderKey = REMIND_KEY_PREFIX + ticket.getSerialNumber() + ":" + senderId + ":" + receiverId + ":" + today;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(reminderKey))) {
            return false;
        }

        sendNotification(member, ticket);
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
