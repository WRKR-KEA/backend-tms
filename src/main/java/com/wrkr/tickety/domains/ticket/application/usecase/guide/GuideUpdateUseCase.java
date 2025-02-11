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
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideDeleteService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideUpdateService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.attachment.FileValidationUtil;
import java.util.List;
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

    private static final int MAX_TOTAL_FILES = 5;
    private final GuideDeleteService guideDeleteService;

    public PkResponse modifyGuide(Long cryptoGuideId, GuideUpdateRequest guideUpdateRequest, List<MultipartFile> newAttachments) {
        Guide guide = guideService.updateGuide(cryptoGuideId, guideUpdateRequest);

        List<GuideAttachment> existingAttachments = guideAttachmentGetService.getAttachmentsByGuideId(guide.getGuideId());

        // 1️⃣ 삭제할 첨부파일 삭제
        if (guideUpdateRequest.deleteAttachments() != null && !guideUpdateRequest.deleteAttachments().isEmpty()) {
            guideUpdateRequest.deleteAttachments().forEach(url -> {
                s3ApiService.deleteObject(url);
                guideAttachmentDeleteService.deleteByFileUrl(url);
            });

            existingAttachments = existingAttachments.stream()
                .filter(attachment -> !guideUpdateRequest.deleteAttachments().contains(attachment.getFileUrl()))
                .toList();
        }

        int remainingFilesCount = existingAttachments.size();
        int newFilesCount = (newAttachments != null) ? newAttachments.size() : 0;
        int totalFilesAfterUpload = remainingFilesCount + newFilesCount;

        if (totalFilesAfterUpload > MAX_TOTAL_FILES) {
            throw new IllegalArgumentException("첨부파일 개수는 최대 " + MAX_TOTAL_FILES + "개까지 가능합니다. 현재 업로드 가능한 파일 개수: "
                + (MAX_TOTAL_FILES - remainingFilesCount));
        }

        // 2️⃣ 새로운 첨부파일 업로드
        if (newAttachments != null && !newAttachments.isEmpty()) {
            FileValidationUtil.validateFiles(newAttachments);

            List<GuideAttachment> uploadedAttachments = newAttachments.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> saveGuideAttachment(guide, file))
                .toList();

            guideAttachmentUploadService.saveAll(uploadedAttachments);
        }

        return guideMapper.guideIdToPkResponse(guide);
    }

    private GuideAttachment saveGuideAttachment(Guide guide, MultipartFile file) {
        String fileUrl = s3ApiService.uploadGuideFile(file);

        return GuideAttachmentMapper.toGuideAttachmentDomain(guide, fileUrl, file.getOriginalFilename(), file.getSize());
    }
}
