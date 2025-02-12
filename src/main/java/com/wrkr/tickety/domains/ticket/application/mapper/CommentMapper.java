package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.attachment.domain.service.CommentAttachmentGetService;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse.CommentDto;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.date.DateTimeFormat;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    private CommentMapper() {
        throw new IllegalArgumentException();
    }

    public static CommentResponse toCommentResponse(Long ticketId, List<Comment> comments, CommentAttachmentGetService commentAttachmentGetService) {
        List<CommentDto> commentDTOList = new ArrayList<>();
        for (Comment comment : comments) {
            if (comment.getMember() == null) {
                commentDTOList.add(CommentDto.builder()
                                       .type("SYSTEM")
                                       .commentId(PkCrypto.encrypt(comment.getCommentId()))
                                       .createdAt(DateTimeFormat.yyyyMMddHHmm(comment.getCreatedAt()))
                                       .content(comment.getContent())
                                       .build());
            } else {
                commentDTOList.add(CommentDto.builder()
                                       .type(comment.getMember().getRole().toString())
                                       .commentId(PkCrypto.encrypt(comment.getCommentId()))
                                       .createdAt(DateTimeFormat.yyyyMMddHHmm(comment.getCreatedAt()))
                                       .memberId(PkCrypto.encrypt(comment.getMember().getMemberId()))
                                       .nickname(comment.getMember().getNickname())
                                       .content(comment.getContent())
                                       .attachments(commentAttachmentGetService.getAttachmentsByCommentId(comment.getCommentId()))
                                       .build());
            }
        }

        return CommentResponse.builder()
            .ticketId(PkCrypto.encrypt(ticketId))
            .comments(commentDTOList)
            .build();
    }
}
