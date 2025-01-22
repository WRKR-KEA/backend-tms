package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.domains.member.persistence.entity.Member;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.global.entity.BaseTimeEntity;
import com.wrkr.tickety.global.model.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseTime {

    private Long ticketId;
    private Member user;
    private Member manager;
    private Category category;
    private String serialNumber;
    private String title;
    private String content;
    private TicketStatus status;
    private Boolean isPinned;

    @Builder
    public Ticket(
            Long ticketId,
            Member user,
            Member manager,
            Category category,
            String serialNumber,
            String title,
            String content,
            TicketStatus status,
            Boolean isPinned
    ) {
        this.ticketId = ticketId;
        this.user = user;
        this.manager = manager;
        this.category = category;
        this.serialNumber = serialNumber;
        this.title = title;
        this.content = content;
        this.status = status;
        this.isPinned = isPinned;
    }

    public void updateStatus(TicketStatus status) {
        this.status = status;
    }
}
