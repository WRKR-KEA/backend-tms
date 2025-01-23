package com.wrkr.tickety.domains.ticket.domain.service.ticket;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketGetService {

    private final TicketPersistenceAdapter ticketPersistenceAdapter;

    public Page<Ticket> getTicketsByUserId(Long userId, Pageable pageable) {
        return ticketPersistenceAdapter.findAllByUserId(userId, pageable);
    }

    public Optional<Ticket> getTicketByTicketId(Long ticketId) {
        return ticketPersistenceAdapter.findById(ticketId);
    }

    public Page<Ticket>getTicketsByManagerFilter(Long managerId, Pageable pageable, String status, boolean isPinned) {
        //todo: status로 필터링 하지 않을 때 어떤 값으로 요청할지 정의 필요
        if (status == null) {
            return ticketPersistenceAdapter.findAllByManagerFilter(managerId, pageable, null, isPinned);
        }
        TicketStatus ticketStatus = TicketStatus.valueOf(status);
        return ticketPersistenceAdapter.findAllByManagerFilter(managerId, pageable, ticketStatus, isPinned);

    }
}
