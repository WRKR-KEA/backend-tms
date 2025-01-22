package com.wrkr.tickety.global.common.mapper;

import com.wrkr.tickety.global.entity.BaseTimeEntity;
import com.wrkr.tickety.global.model.BaseTime;
import org.mapstruct.InheritInverseConfiguration;

public interface PersistenceMapper<ENTITY extends BaseTimeEntity, DOMAIN extends BaseTime> {

    ENTITY toEntity(final DOMAIN domain);

    @InheritInverseConfiguration
    DOMAIN toDomain(final ENTITY entity);
}
