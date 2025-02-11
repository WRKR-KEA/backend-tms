package com.wrkr.tickety.domains.attachment.domain.service;

import com.wrkr.tickety.domains.attachment.domain.model.GuideAttachment;
import com.wrkr.tickety.domains.attachment.persistence.adapter.GuideAttachmentPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GuideAttachmentDeleteService {

    private final GuideAttachmentPersistenceAdapter guideAttachmentPersistenceAdapter;


    public void deleteAttachment(GuideAttachment attachment) {
        guideAttachmentPersistenceAdapter.delete(attachment);
    }

    public void deleteByFileUrl(String fileUrl) {
        guideAttachmentPersistenceAdapter.deleteByFileUrl(fileUrl);
    }
}
