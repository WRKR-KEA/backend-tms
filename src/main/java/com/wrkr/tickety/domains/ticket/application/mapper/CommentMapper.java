package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse.CommentDTO;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    private CommentMapper() {
        throw new IllegalArgumentException();
    }

    public static CommentResponse toCommentResponse(Long ticketId, List<Comment> comments) {
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (Comment comment : comments) {
            commentDTOList.add(new CommentDTO(
                comment.getMember() == null ? "SYSTEM" : comment.getMember().getRole().toString(),
                PkCrypto.encrypt(comment.getCommentId()),
                comment.getCreatedAt(),
                PkCrypto.encrypt(comment.getMember().getMemberId()),
                comment.getMember().getName(),
                comment.getContent()
            ));
        }

        return new CommentResponse(PkCrypto.encrypt(ticketId), commentDTOList);
    }
}
