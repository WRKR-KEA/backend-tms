package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "ticket")
public class Ticket extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Member manager;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, unique = true)
    private String serialNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Boolean isPinned;

    @Builder
    public Ticket(Member user, Member manager, Category category, String serialNumber, String title, String content, TicketStatus status) {
        this.user = user;
        this.manager = manager;
        this.category = category;
        this.serialNumber = serialNumber;
        this.title = title;
        this.content = content;
        this.status = status;
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

    public boolean isCommentable() {
        return status.equals(TicketStatus.IN_PROGRESS);
    }
}
