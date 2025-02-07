package com.wrkr.tickety.domains.attachment.domain.service;

import com.wrkr.tickety.domains.attachment.domain.model.CommentAttachment;
import com.wrkr.tickety.domains.attachment.persistence.adapter.CommentAttachmentPersistenceAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentAttachmentUploadService {

    private final CommentAttachmentPersistenceAdapter commentAttachmentPersistenceAdapter;

    public void saveAll(List<CommentAttachment> attachments) {
        commentAttachmentPersistenceAdapter.saveAll(attachments);
    }
}
