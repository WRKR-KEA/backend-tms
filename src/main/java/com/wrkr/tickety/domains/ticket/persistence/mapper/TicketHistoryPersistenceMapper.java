package com.wrkr.tickety.domains.ticket.persistence.mapper;

import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketHistoryEntity;
import com.wrkr.tickety.global.common.mapper.PersistenceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketHistoryPersistenceMapper extends PersistenceMapper<TicketHistoryEntity, TicketHistory> {

}
