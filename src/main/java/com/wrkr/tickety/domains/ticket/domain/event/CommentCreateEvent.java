package com.wrkr.tickety.domains.ticket.domain.event;

import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import lombok.Builder;

@Builder
public record CommentCreateEvent(
    Comment comment
) {

}
