package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.attachment.domain.service.CommentAttachmentGetService;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse.CommentDto;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    private CommentMapper() {
        throw new IllegalArgumentException();
    }

    public static CommentResponse toCommentResponse(Long ticketId, List<Comment> comments, CommentAttachmentGetService commentAttachmentGetService) {
        List<CommentDto> commentDTOList = new ArrayList<>();
        for (Comment comment : comments) {
            commentDTOList.add(CommentDto.builder()
                .memberId(comment.getMember() == null ? "SYSTEM" : comment.getMember().getRole().toString())
                .commentId(PkCrypto.encrypt(comment.getCommentId()))
                .createdAt(comment.getCreatedAt())
                .memberId(PkCrypto.encrypt(comment.getMember().getMemberId()))
                .nickname(comment.getMember().getName())
                .content(comment.getContent())
                .attachments(commentAttachmentGetService.getAttachmentsByCommentId(comment.getCommentId()))
                .build());
        }

        return CommentResponse.builder()
            .ticketId(PkCrypto.encrypt(ticketId))
            .comments(commentDTOList)
            .build();
    }
}
