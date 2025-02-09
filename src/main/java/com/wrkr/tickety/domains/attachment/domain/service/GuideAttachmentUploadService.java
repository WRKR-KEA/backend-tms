package com.wrkr.tickety.domains.attachment.domain.service;

import com.wrkr.tickety.domains.attachment.domain.model.GuideAttachment;
import com.wrkr.tickety.domains.attachment.persistence.adapter.GuideAttachmentPersistenceAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GuideAttachmentUploadService {

    private final GuideAttachmentPersistenceAdapter guideAttachmentPersistenceAdapter;


    public void saveAll(List<GuideAttachment> attachments) {
        guideAttachmentPersistenceAdapter.saveAll(attachments);
    }
}
