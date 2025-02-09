package com.wrkr.tickety.domains.attachment.domain.service;

import com.wrkr.tickety.domains.attachment.domain.model.CommentAttachment;
import com.wrkr.tickety.domains.attachment.persistence.adapter.CommentAttachmentPersistenceAdapter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentAttachmentGetService {


    private final CommentAttachmentPersistenceAdapter commentAttachmentPersistenceAdapter;

    public List<String> getAttachmentsByCommentId(Long commentId) {
        List<CommentAttachment> attachments = commentAttachmentPersistenceAdapter.getCommentAttachmentsByCommentId(commentId);
        return attachments.stream()
            .map(CommentAttachment::getFileUrl)
            .collect(Collectors.toList());
    }

}
