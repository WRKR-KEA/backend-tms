package com.wrkr.tickety.domains.ticket.domain.service;

import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketHistorySaveService {

    private final TicketHistoryRepository ticketHistoryRepository;

    public void save(TicketHistory ticketHistory) {
        ticketHistoryRepository.save(ticketHistory);
    }
}
