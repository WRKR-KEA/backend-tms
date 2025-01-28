package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse.TicketCount;
import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketHistoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TicketHistoryPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TicketPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketHistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketHistoryPersistenceAdapter {

    private final TicketHistoryRepository ticketHistoryRepository;
    private final TicketPersistenceMapper ticketPersistenceMapper;
    private final TicketHistoryPersistenceMapper ticketHistoryPersistenceMapper;

    public TicketHistory save(final TicketHistory ticketHistory) {
        TicketHistoryEntity ticketHistoryEntity = this.ticketHistoryPersistenceMapper.toEntity(ticketHistory);
        TicketHistoryEntity savedEntity = this.ticketHistoryRepository.save(ticketHistoryEntity);
        return this.ticketHistoryPersistenceMapper.toDomain(savedEntity);
    }

    public Optional<TicketHistory> findFirstByTicketIdAndModifiedOrderByCreatedAtAsc(Long ticketId, ModifiedType modifiedType) {
        TicketHistoryEntity ticketHistoryEntity = ticketHistoryRepository.findFirstByTicket_TicketIdAndModifiedOrderByCreatedAtAsc(
            ticketId,
            modifiedType
        );
        return Optional.ofNullable(ticketHistoryPersistenceMapper.toDomain(ticketHistoryEntity));
    }

    public List<TicketCount> countByTicketStatusDuringPeriod(
        LocalDateTime startDate,
        LocalDateTime endDate,
        StatisticsType statisticsType,
        TicketStatus ticketStatus
    ) {
        return this.ticketHistoryRepository.countByTicketStatusDuringPeriod(
            startDate,
            endDate,
            statisticsType,
            ticketStatus
        );
    }

    public Optional<TicketHistory> findByTicketAndChangedStatus(Ticket ticket, TicketStatus status) {
        TicketEntity ticketEntity = ticketPersistenceMapper.toEntity(ticket);
        TicketHistoryEntity ticketHistoryEntity = ticketHistoryRepository.findTop1ByTicketAndModifiedAndStatusOrderByTicketHistoryIdDesc(
            ticketEntity, ModifiedType.STATUS, status);
        return Optional.ofNullable(ticketHistoryPersistenceMapper.toDomain(ticketHistoryEntity));
    }
}
