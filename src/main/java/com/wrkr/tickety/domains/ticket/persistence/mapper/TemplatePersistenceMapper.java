package com.wrkr.tickety.domains.ticket.persistence.mapper;

import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.persistence.entity.TemplateEntity;
import com.wrkr.tickety.global.common.mapper.PersistenceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TemplatePersistenceMapper extends PersistenceMapper<TemplateEntity, Template> {

}
