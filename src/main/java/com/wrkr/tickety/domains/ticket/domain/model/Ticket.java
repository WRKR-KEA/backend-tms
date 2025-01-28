package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.global.model.BaseTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    public void updateManager(Member manager) {
        this.manager = manager;
    }

    public boolean isRelatedWith(Member member) {
        if (member.getRole().equals(Role.MANAGER)) {
            return this.manager.equals(member);
        } else {
            return this.user.equals(member);
        }
    }

    public boolean isAccessibleBy(Member member) {
        if (member.getRole().equals(Role.USER)) {
            return this.user.equals(member);
        } else {
            return true;
        }
    }

    public void approveTicket(Member member) {
        this.status = TicketStatus.IN_PROGRESS;
        this.manager = member;
    }

    public boolean isCommentable() {
        return this.status.equals(TicketStatus.IN_PROGRESS);
    }

    public boolean isApprovable() {
        return this.status.equals(TicketStatus.REQUEST) && manager == null;
    }

    public boolean isDelegatable() {
        return this.status.equals(TicketStatus.IN_PROGRESS);
    }

    public boolean hasManager() {
        return this.manager != null;
    }

    public boolean isManagedBy(Long managerId) {
        return this.manager != null && this.manager.getMemberId().equals(managerId);
    }
}
