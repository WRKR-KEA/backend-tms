package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.domains.member.persistence.entity.Member;
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
public class Comment extends BaseTime {

    private Long commentId;
    private Ticket ticket;
    private Member member;
    private String content;

    @Builder
    public Comment(
            Long commentId,
            Ticket ticket,
            Member member,
            String content
    ) {
        this.commentId = commentId;
        this.ticket = ticket;
        this.member = member;
        this.content = content;
    }
}

