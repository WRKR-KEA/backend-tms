package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.attachment.application.mapper.GuideAttachmentMapper;
import com.wrkr.tickety.domains.attachment.domain.model.GuideAttachment;
import com.wrkr.tickety.domains.attachment.domain.service.GuideAttachmentDeleteService;
import com.wrkr.tickety.domains.attachment.domain.service.GuideAttachmentGetService;
import com.wrkr.tickety.domains.attachment.domain.service.GuideAttachmentUploadService;
import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideUpdateService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@UseCase
@RequiredArgsConstructor
@Transactional
public class GuideUpdateUseCase {

    private final GuideUpdateService guideService;
    private final GuideAttachmentGetService guideAttachmentGetService;
    private final GuideAttachmentDeleteService guideAttachmentDeleteService;
    private final GuideAttachmentUploadService guideAttachmentUploadService;
    private final GuideMapper guideMapper;
    private final S3ApiService s3ApiService;

    public PkResponse modifyGuide(Long cryptoGuideId, GuideUpdateRequest guideUpdateRequest) {
        Guide guide = guideService.updateGuide(cryptoGuideId, guideUpdateRequest);

        // 1️⃣ 기존 첨부파일 가져오기
        List<GuideAttachment> existingAttachments = guideAttachmentGetService.getAttachmentsByGuideId(guide.getGuideId());

        // 2️⃣ 기존 첨부파일 삭제 (S3 및 DB)
        existingAttachments.forEach(attachment -> {
            s3ApiService.deleteObject(attachment.getFileUrl());
            guideAttachmentDeleteService.deleteAttachment(attachment);
        });

        // 3️⃣ 새로운 파일 업로드 (만약 새 파일이 존재하는 경우)
        if (guideUpdateRequest.attachments() != null && !guideUpdateRequest.attachments().isEmpty()) {
            List<GuideAttachment> newAttachments = guideUpdateRequest.attachments().stream()
                .filter(file -> !file.isEmpty())
                .map(file -> saveGuideAttachment(guide, file))
                .collect(Collectors.toList());

            if (!newAttachments.isEmpty()) {
                guideAttachmentUploadService.saveAll(newAttachments);
            }
        }

        return guideMapper.guideIdToPkResponse(guide);
    }

    private GuideAttachment saveGuideAttachment(Guide guide, MultipartFile file) {
        String fileUrl = s3ApiService.uploadGuideFile(file);

        return GuideAttachmentMapper.toGuideAttachmentDomain(guide, fileUrl, file.getOriginalFilename(), file.getSize());
    }
}
