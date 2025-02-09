package com.wrkr.tickety.domains.attachment.persistence.mapper;

import com.wrkr.tickety.domains.attachment.domain.model.CommentAttachment;
import com.wrkr.tickety.domains.attachment.persistence.entity.CommentAttachmentEntity;
import com.wrkr.tickety.global.common.mapper.PersistenceMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentAttachmentPersistenceMapper extends PersistenceMapper<CommentAttachmentEntity, CommentAttachment> {

    @Override
    @Mapping(target = "attachmentId", ignore = true)
    CommentAttachmentEntity toEntity(CommentAttachment domain);

    List<CommentAttachmentEntity> toEntities(List<CommentAttachment> domains);
}
