package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.model.BaseTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

