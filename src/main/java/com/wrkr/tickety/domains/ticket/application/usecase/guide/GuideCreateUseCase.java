package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.attachment.application.mapper.GuideAttachmentMapper;
import com.wrkr.tickety.domains.attachment.domain.model.GuideAttachment;
import com.wrkr.tickety.domains.attachment.domain.service.GuideAttachmentUploadService;
import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.ticket.application.dto.request.GuideCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideCreateService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@UseCase
@RequiredArgsConstructor
@Transactional
public class GuideCreateUseCase {

    private final GuideCreateService guideCreateService;
    private final CategoryGetService categoryGetService;
    private final GuideAttachmentUploadService guideAttachmentUploadService;
    private final GuideMapper guideMapper;
    private final S3ApiService s3ApiService;

    public PkResponse createGuide(GuideCreateRequest guideCreateRequest, Long categoryId, List<MultipartFile> guideAttachments) {

        Category category = categoryGetService.getParentCategory(categoryId);
        Guide guide = Guide.builder()
            .content(guideCreateRequest.content())
            .category(category)
            .build();

        Guide savedGuide = guideCreateService.createGuide(guide);
        List<GuideAttachment> attachments;
        if (guideAttachments != null && !guideAttachments.isEmpty()) {
            attachments = new ArrayList<>();
            guideAttachments.forEach(attachmentFile -> {
                String fileUrl = s3ApiService.uploadGuideFile(attachmentFile);
                GuideAttachment guideAttachment = GuideAttachmentMapper.toGuideAttachmentDomain(savedGuide, fileUrl,
                                                                                                attachmentFile.getOriginalFilename(),
                                                                                                attachmentFile.getSize());
                attachments.add(guideAttachment);
            });

            guideAttachmentUploadService.saveAll(attachments);
        }

        return guideMapper.guideIdToPkResponse(savedGuide);
    }
}
