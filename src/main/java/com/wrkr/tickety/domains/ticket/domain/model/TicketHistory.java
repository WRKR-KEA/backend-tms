package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.global.entity.BaseTimeEntity;
import com.wrkr.tickety.global.model.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketHistory extends BaseTime {

    private Long ticketHistoryId;
    private Ticket ticket;
    private Member manager;
    private TicketStatus status;
    private ModifiedType modified;

    @Builder
    public TicketHistory(
            Long ticketHistoryId,
            Ticket ticket,
            Member manager,
            TicketStatus status,
            ModifiedType modified
    ) {
        this.ticketHistoryId = ticketHistoryId;
        this.ticket = ticket;
        this.manager = manager;
        this.status = status;
        this.modified = modified;
    }
}
