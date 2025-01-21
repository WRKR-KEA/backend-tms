package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {

    Optional<TicketHistory> findFirstByTicket_TicketIdAndModifiedOrderByCreatedAtAsc(
            Long ticketId,
            ModifiedType modifiedType
    );
}
