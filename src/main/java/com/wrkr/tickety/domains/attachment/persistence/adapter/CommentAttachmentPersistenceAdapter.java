package com.wrkr.tickety.domains.attachment.persistence.adapter;

import com.wrkr.tickety.domains.attachment.domain.model.CommentAttachment;
import com.wrkr.tickety.domains.attachment.persistence.entity.CommentAttachmentEntity;
import com.wrkr.tickety.domains.attachment.persistence.mapper.CommentAttachmentPersistenceMapper;
import com.wrkr.tickety.domains.attachment.persistence.repository.CommentAttachmentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentAttachmentPersistenceAdapter {

    private final CommentAttachmentRepository commentAttachmentRepository;
    private final CommentAttachmentPersistenceMapper commentAttachmentPersistenceMapper;

    public void saveAll(List<CommentAttachment> attachments) {
        List<CommentAttachmentEntity> attachmentEntities = commentAttachmentPersistenceMapper.toEntities(attachments);
        commentAttachmentRepository.saveAll(attachmentEntities);
    }

    public List<CommentAttachment> getCommentAttachmentsByCommentId(Long commentId) {
        List<CommentAttachmentEntity> attachmentEntities = commentAttachmentRepository.findByComment_CommentId(commentId);
        return attachmentEntities.stream()
            .map(commentAttachmentPersistenceMapper::toDomain)
            .collect(Collectors.toList());
    }
}
