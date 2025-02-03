package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.domain.constant.SortType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TicketPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketPersistenceAdapter {

    private final TicketRepository ticketRepository;
    private final TicketPersistenceMapper ticketPersistenceMapper;

    public Ticket save(final Ticket ticket) {

        TicketEntity ticketEntity = this.ticketPersistenceMapper.toEntity(ticket);
        TicketEntity savedEntity = this.ticketRepository.save(ticketEntity);
        return this.ticketPersistenceMapper.toDomain(savedEntity);
    }

    public Page<Ticket> findAllByUserId(final Long userId, final Pageable pageable) {

        return ticketRepository.findAllByUserId(userId, pageable)
            .map(this.ticketPersistenceMapper::toDomain);
    }

    public Ticket findById(final Long ticketId) {

        final TicketEntity ticketEntity = this.ticketRepository.findById(ticketId)
            .orElseThrow(() -> ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND));
        return this.ticketPersistenceMapper.toDomain(ticketEntity);
    }

    public Page<Ticket> findAll(final String query, final TicketStatus status, final LocalDate startDate, final LocalDate endDate, final Pageable pageable) {
        Page<TicketEntity> ticketEntityPage = ticketRepository.getAll(query, status, startDate, endDate, pageable);
        return ticketEntityPage.map(ticketPersistenceMapper::toDomain);
    }

    public Long findTicketCountByCategoryAndDateRange(final Long categoryId, final LocalDateTime start, final LocalDateTime end) {
        return ticketRepository.findTicketCountByCategoryAndDateRange(categoryId, start, end);
    }

    public Page<Ticket> findAllByManagerFilter(final Long managerId, final Pageable pageable, final TicketStatus status, final String query,
        final SortType sortType) {
        return ticketRepository.findByManagerFilters(managerId, status, pageable, query, sortType).map(this.ticketPersistenceMapper::toDomain);
    }
}
