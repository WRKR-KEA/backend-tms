package com.wrkr.tickety.domains.log.persistence.mapper;

import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.persistence.entity.AccessLogEntity;
import com.wrkr.tickety.global.common.mapper.PersistenceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccessLogPersistenceMapper extends PersistenceMapper<AccessLogEntity, AccessLog> {

}
