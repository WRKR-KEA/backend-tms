package com.wrkr.tickety.domains.ticket.application.usecase.comment;

import com.wrkr.tickety.domains.attachment.application.mapper.CommentAttachmentMapper;
import com.wrkr.tickety.domains.attachment.domain.model.CommentAttachment;
import com.wrkr.tickety.domains.attachment.domain.service.CommentAttachmentUploadService;
import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.CommentRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.event.CommentCreateEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.comment.CommentSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.exception.CommentErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.attachment.FileValidationUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@UseCase
@Transactional
@RequiredArgsConstructor
public class CommentCreateUseCase {

    private final TicketGetService ticketGetService;
    private final CommentSaveService commentSaveService;
    private final CommentAttachmentUploadService commentAttachmentUploadService;
    private final S3ApiService s3ApiService;
    private final ApplicationEventPublisher applicationEventPublisher;


    public PkResponse createComment(Member member, Long ticketId, CommentRequest request, List<MultipartFile> commentAttachments) {

        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId);

        if (!ticket.isRelatedWith(member)) {
            throw ApplicationException.from(TicketErrorCode.UNAUTHORIZED_ACCESS);
        }
        if (!ticket.isCommentable()) {
            throw ApplicationException.from(CommentErrorCode.COMMENT_CONFLICT);
        }

        Comment comment = Comment.builder()
            .ticket(ticket)
            .member(member)
            .content(request.content())
            .build();

        Comment savedComment = commentSaveService.saveComment(comment);

        if (commentAttachments != null && !commentAttachments.isEmpty()) {
            FileValidationUtil.validateFiles(commentAttachments);

            List<CommentAttachment> validAttachments = commentAttachments.stream()
                .filter(file -> !file.isEmpty()) // 빈 파일 필터링
                .map(file ->
                    saveCommentAttachment(savedComment, file))
                .toList();

            commentAttachmentUploadService.saveAll(validAttachments);
        }

        applicationEventPublisher.publishEvent(CommentCreateEvent.builder()
            .comment(savedComment)
            .build());

        return PkResponse.builder()
            .id(PkCrypto.encrypt(savedComment.getCommentId()))
            .build();
    }

    private CommentAttachment saveCommentAttachment(Comment comment, MultipartFile file) {
        String fileUrl = s3ApiService.uploadCommentFile(file);
        return CommentAttachmentMapper.toCommentAttachmentDomain(comment, fileUrl, file.getOriginalFilename(), file.getSize());
    }
}
