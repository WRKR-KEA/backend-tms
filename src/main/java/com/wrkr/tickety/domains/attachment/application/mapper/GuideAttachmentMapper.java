package com.wrkr.tickety.domains.attachment.application.mapper;

import com.wrkr.tickety.domains.attachment.domain.model.GuideAttachment;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;

public class GuideAttachmentMapper {

    public static GuideAttachment toGuideAttachmentDomain(Guide guide, String fileUrl, String originalFilename, long size) {
        return GuideAttachment.builder()
            .guide(guide)
            .fileUrl(fileUrl)
            .fileName(originalFilename)
            .fileSize(size)
            .build();
    }

}
