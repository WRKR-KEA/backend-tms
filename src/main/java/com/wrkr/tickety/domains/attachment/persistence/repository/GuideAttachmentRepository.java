package com.wrkr.tickety.domains.attachment.persistence.repository;

import com.wrkr.tickety.domains.attachment.persistence.entity.GuideAttachmentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideAttachmentRepository extends JpaRepository<GuideAttachmentEntity, Long> {


    List<GuideAttachmentEntity> findByGuide_GuideId(Long guideId);
}
