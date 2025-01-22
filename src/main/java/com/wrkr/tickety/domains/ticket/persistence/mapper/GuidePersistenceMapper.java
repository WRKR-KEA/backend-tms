package com.wrkr.tickety.domains.ticket.persistence.mapper;

import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.persistence.entity.GuideEntity;
import com.wrkr.tickety.global.common.mapper.PersistenceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GuidePersistenceMapper extends PersistenceMapper<GuideEntity, Guide> {

}
