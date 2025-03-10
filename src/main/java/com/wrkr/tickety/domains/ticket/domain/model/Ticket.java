package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.global.model.BaseTime;
import lombok.AccessLevel;
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
    private Integer version;

    public void updateStatus(TicketStatus status) {
        this.status = status;
    }

    public void updateManager(Member manager) {
        this.manager = manager;
        this.isPinned = false;
    }

    public boolean isRelatedWith(Member member) {
        if (member.getRole().equals(Role.MANAGER)) {
            if (this.getStatus().compareTo(TicketStatus.CANCEL) < 0) {
                return false;
            }
            return this.manager.getMemberId().equals(member.getMemberId());
        } else {
            return this.user.getMemberId().equals(member.getMemberId());
        }
    }

    public boolean isAccessibleBy(Member member) {
        if (member.getRole().equals(Role.USER)) {
            return this.user.getMemberId().equals(member.getMemberId());
        } else {
            return !this.getStatus().equals(TicketStatus.CANCEL);
        }
    }

    public void approveTicket(Member member) {
        this.status = TicketStatus.IN_PROGRESS;
        this.manager = member;
    }

    public void pinTicket(Ticket ticket) {
        this.isPinned = !this.isPinned;
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

    public boolean isTicketStatus(TicketStatus status) {
        return status.equals(this.status);
    }
}
