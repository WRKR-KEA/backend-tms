package com.wrkr.tickety.domains.attachment.persistence.repository;

import com.wrkr.tickety.domains.attachment.persistence.entity.CommentAttachmentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentAttachmentRepository extends JpaRepository<CommentAttachmentEntity, Long> {

    List<CommentAttachmentEntity> findByComment_CommentId(Long commentId);
}
