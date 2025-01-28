package com.wrkr.tickety.domains.ticket.persistence.repository;

import static com.wrkr.tickety.domains.ticket.persistence.entity.QTicketEntity.ticketEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketQueryDslRepositoryImpl implements TicketQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<TicketEntity> getAll(String query, TicketStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        List<TicketEntity> ticketEntityList = jpaQueryFactory
            .selectFrom(ticketEntity)
            .where(
                titleOrManagerNicknameOrSerialNumberContainsIgnoreCase(query),
                statusEq(status),
                createdAtGoe(startDate),
                createdAtLoe(endDate)
            )
            .orderBy(ticketEntity.ticketId.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(ticketEntity.count())
            .from(ticketEntity)
            .where(
                titleOrManagerNicknameOrSerialNumberContainsIgnoreCase(query),
                statusEq(status),
                createdAtGoe(startDate),
                createdAtLoe(endDate)
            );

        return PageableExecutionUtils.getPage(
            ticketEntityList,
            pageable,
            countQuery::fetchOne
        );
    }

    private BooleanExpression titleOrManagerNicknameOrSerialNumberContainsIgnoreCase(String query) {
        return query == null ? null : ticketEntity.title.containsIgnoreCase(query)
            .or(ticketEntity.manager.nickname.containsIgnoreCase(query))
            .or(ticketEntity.serialNumber.containsIgnoreCase(query));
    }

    private BooleanExpression statusEq(TicketStatus status) {
        return status == null ? null : ticketEntity.status.eq(status);
    }

    private BooleanExpression createdAtGoe(LocalDate startDate) {
        return startDate == null ? null : ticketEntity.createdAt.goe(LocalDateTime.of(startDate, LocalTime.MIN));
    }

    private BooleanExpression createdAtLoe(LocalDate endDate) {
        return endDate == null ? null : ticketEntity.createdAt.loe(LocalDateTime.of(endDate, LocalTime.MAX));
    }
}
