package com.wrkr.tickety.domains.notification.persistence.mapper;

import com.wrkr.tickety.domains.notification.domain.model.Notification;
import com.wrkr.tickety.domains.notification.persistence.entity.NotificationEntity;
import com.wrkr.tickety.global.common.mapper.PersistenceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationPersistenceMapper extends PersistenceMapper<NotificationEntity, Notification> {

}
