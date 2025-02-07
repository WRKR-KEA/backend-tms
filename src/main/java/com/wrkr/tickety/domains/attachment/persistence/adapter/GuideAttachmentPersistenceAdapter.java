package com.wrkr.tickety.domains.attachment.persistence.adapter;

import com.wrkr.tickety.domains.attachment.domain.model.GuideAttachment;
import com.wrkr.tickety.domains.attachment.persistence.entity.GuideAttachmentEntity;
import com.wrkr.tickety.domains.attachment.persistence.mapper.GuideAttachmentPersistenceMapper;
import com.wrkr.tickety.domains.attachment.persistence.repository.GuideAttachmentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GuideAttachmentPersistenceAdapter {

    private final GuideAttachmentRepository guideAttachmentRepository;
    private final GuideAttachmentPersistenceMapper guideAttachmentPersistenceMapper;

    public void saveAll(List<GuideAttachment> attachments) {
        List<GuideAttachmentEntity> attachmentEntities = guideAttachmentPersistenceMapper.toEntities(attachments);
        guideAttachmentRepository.saveAll(attachmentEntities);
    }

    public List<GuideAttachment> getGuideAttachmentsByGuideId(Long guideId) {
        List<GuideAttachmentEntity> attachmentEntities = guideAttachmentRepository.findByGuide_GuideId(guideId);
        return attachmentEntities.stream()
            .map(guideAttachmentPersistenceMapper::toDomain)
            .collect(Collectors.toList());
    }

    public void delete(GuideAttachment attachment) {
        guideAttachmentRepository.deleteById(attachment.getAttachmentId());
    }
}
