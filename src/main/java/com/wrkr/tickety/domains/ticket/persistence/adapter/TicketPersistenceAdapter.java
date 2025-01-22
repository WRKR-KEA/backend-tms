package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TicketPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
        return ticketRepository.findAllByUserId(userId, pageable).map(this.ticketPersistenceMapper::toDomain);
    }

    public Optional<Ticket> findById(final Long ticketId) {
        final Optional<TicketEntity> ticketEntity = this.ticketRepository.findById(ticketId);
        return ticketEntity.map(this.ticketPersistenceMapper::toDomain);
    }
}
