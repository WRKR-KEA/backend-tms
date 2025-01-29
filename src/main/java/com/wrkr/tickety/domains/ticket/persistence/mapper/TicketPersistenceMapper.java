package com.wrkr.tickety.domains.ticket.persistence.mapper;

import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.global.common.mapper.PersistenceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CategoryPersistenceMapper.class})
public interface TicketPersistenceMapper extends PersistenceMapper<TicketEntity, Ticket> {

}
