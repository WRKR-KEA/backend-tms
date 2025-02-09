package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.attachment.domain.model.GuideAttachment;
import com.wrkr.tickety.domains.attachment.domain.service.GuideAttachmentDeleteService;
import com.wrkr.tickety.domains.attachment.domain.service.GuideAttachmentGetService;
import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideDeleteService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class GuideDeleteUseCase {

    private final GuideDeleteService guideDeleteService;
    private final GuideAttachmentGetService guideAttachmentGetService;
    private final GuideAttachmentDeleteService guideAttachmentDeleteService;
    private final S3ApiService s3ApiService;

    public PkResponse deleteGuide(Long guideId) {

        List<GuideAttachment> existingAttachments = guideAttachmentGetService.getAttachmentsByGuideId(guideId);

        existingAttachments.forEach(attachment -> {
            s3ApiService.deleteObject(attachment.getFileUrl());
            guideAttachmentDeleteService.deleteAttachment(attachment);
        });

        PkResponse pkResponse = guideDeleteService.deleteGuide(guideId);

        return pkResponse;
    }
}
