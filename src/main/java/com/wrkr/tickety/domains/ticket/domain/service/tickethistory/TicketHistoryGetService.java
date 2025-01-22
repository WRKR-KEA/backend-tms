package com.wrkr.tickety.domains.ticket.domain.service.tickethistory;

import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketHistoryGetService {

    private final TicketHistoryRepository ticketHistoryRepository;


    public LocalDateTime getFirstManagerChangeDate(Long ticketId) {
        return ticketHistoryRepository.findFirstByTicket_TicketIdAndModifiedOrderByCreatedAtAsc(
                ticketId,
                ModifiedType.MANAGER
        ).map(TicketHistory::getCreatedAt).orElse(null);
    }
}
