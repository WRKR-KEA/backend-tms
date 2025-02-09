package com.wrkr.tickety.domains.attachment.application.mapper;

import com.wrkr.tickety.domains.attachment.domain.model.CommentAttachment;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;

public class CommentAttachmentMapper {


    public static CommentAttachment toCommentAttachmentDomain(Comment comment, String fileUrl, String originalFilename, long size) {
        return CommentAttachment.builder()
            .comment(comment)
            .fileUrl(fileUrl)
            .fileName(originalFilename)
            .fileSize(size)
            .build();
    }
}
