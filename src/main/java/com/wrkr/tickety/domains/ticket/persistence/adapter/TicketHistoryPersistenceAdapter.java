package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketHistoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TicketHistoryPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketHistoryPersistenceAdapter {

    private final TicketHistoryRepository ticketHistoryRepository;
    private final TicketHistoryPersistenceMapper ticketHistoryPersistenceMapper;

    public TicketHistory save(final TicketHistory ticketHistory) {
        TicketHistoryEntity ticketHistoryEntity = this.ticketHistoryPersistenceMapper.toEntity(ticketHistory);
        TicketHistoryEntity savedEntity = this.ticketHistoryRepository.save(ticketHistoryEntity);
        return this.ticketHistoryPersistenceMapper.toDomain(savedEntity);
    }
}
