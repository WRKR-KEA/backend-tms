package com.wrkr.tickety.domains.ticket.domain.service.tickethistory;

import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketHistoryPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketHistorySaveService {

    private final TicketHistoryPersistenceAdapter ticketHistoryPersistenceAdapter;

    public void save(TicketHistory ticketHistory) {
        ticketHistoryPersistenceAdapter.save(ticketHistory);
    }
}
