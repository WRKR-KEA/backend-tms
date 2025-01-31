package com.wrkr.tickety.domains.ticket.persistence.repository;

import static com.wrkr.tickety.domains.ticket.persistence.entity.QTicketEntity.ticketEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wrkr.tickety.domains.ticket.domain.constant.SortType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TicketQueryDslRepositoryImpl implements TicketQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TicketEntity> findByManagerFilters(
        Long managerId,
        TicketStatus status,
        Pageable pageable,
        String search,
        SortType sortType
    ) {
        var orderSpecifiers = new ArrayList<OrderSpecifier<?>>();
        orderSpecifiers.add(ticketEntity.isPinned.desc());
        if (sortType != null) {
            orderSpecifiers.add(sortType == SortType.NEWEST
                                    ? ticketEntity.createdAt.desc()
                                    : ticketEntity.createdAt.asc());
        }


        List<TicketEntity> ticketEntityList = queryFactory.selectFrom(ticketEntity)
            .where(
                ticketEntity.manager.memberId.eq(managerId),
                status != null ? ticketEntity.status.eq(status) : null,
                search != null ? ticketEntity.serialNumber.contains(search)
                    .or(ticketEntity.content.contains(search)) : null
            )
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> total = queryFactory.select(ticketEntity.count())
            .from(ticketEntity)
            .where(
                ticketEntity.manager.memberId.eq(managerId),
                status != null ? ticketEntity.status.eq(status) : null,
                search != null ? ticketEntity.serialNumber.contains(search)
                    .or(ticketEntity.content.contains(search)) : null
            );

        return PageableExecutionUtils.getPage(
            ticketEntityList,
            pageable,
            total::fetchOne
        );

    }

}
