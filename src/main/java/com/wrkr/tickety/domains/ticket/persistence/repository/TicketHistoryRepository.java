package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketHistoryRepository extends JpaRepository<TicketHistoryEntity, Long> {

    TicketHistoryEntity findFirstByTicket_TicketIdAndModifiedOrderByCreatedAtAsc(
        Long ticketId,
        ModifiedType modifiedType
    );

    TicketHistoryEntity findTop1ByTicketAndModifiedAndStatusOrderByTicketHistoryIdDesc(TicketEntity ticket, ModifiedType modifiedType, TicketStatus status);
}
