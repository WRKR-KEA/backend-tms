package com.wrkr.tickety.domains.attachment.persistence.mapper;

import com.wrkr.tickety.domains.attachment.domain.model.GuideAttachment;
import com.wrkr.tickety.domains.attachment.persistence.entity.GuideAttachmentEntity;
import com.wrkr.tickety.global.common.mapper.PersistenceMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GuideAttachmentPersistenceMapper extends PersistenceMapper<GuideAttachmentEntity, GuideAttachment> {

    @Override
    @Mapping(target = "attachmentId", ignore = true)
    GuideAttachmentEntity toEntity(GuideAttachment domain);

    List<GuideAttachmentEntity> toEntities(List<GuideAttachment> domains);


}
