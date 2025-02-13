package com.wrkr.tickety.domains.ticket.persistence.repository;

import static com.wrkr.tickety.domains.ticket.persistence.entity.QTicketEntity.ticketEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketPreResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.QDepartmentTicketPreResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.SortType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.QTicketEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketQueryDslRepositoryImpl implements TicketQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // MEMO: isManagerAccessible() 조건은 담당자가 조회하는 경우에만 필요
    @Override
    public Page<TicketEntity> getAll(String query, TicketStatus status, LocalDate startDate, LocalDate endDate, ApplicationPageRequest pageRequest) {

        var orderSpecifiers = getOrderSpecifier(pageRequest.sortType());

        List<TicketEntity> ticketEntityList = jpaQueryFactory
            .selectFrom(ticketEntity)
            .where(
                titleOrManagerNicknameOrSerialNumberContainsIgnoreCase(query),
                statusEq(status),
                createdAtGoe(startDate),
                createdAtLoe(endDate),
                isManagerAccessible()
            )
            .orderBy(orderSpecifiers)
            .offset((long) pageRequest.size() * pageRequest.page())
            .limit(pageRequest.size())
            .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(ticketEntity.count())
            .from(ticketEntity)
            .where(
                titleOrManagerNicknameOrSerialNumberContainsIgnoreCase(query),
                statusEq(status),
                createdAtGoe(startDate),
                createdAtLoe(endDate),
                isManagerAccessible()
            );

        return PageableExecutionUtils.getPage(
            ticketEntityList,
            pageRequest.toPageable(),
            countQuery::fetchOne
        );
    }

    @Override
    public List<DepartmentTicketPreResponse> getAllTicketsNoPaging(String query, TicketStatus status, LocalDate startDate, LocalDate endDate) {
        QTicketEntity t = ticketEntity;

        return jpaQueryFactory
            .select(
                new QDepartmentTicketPreResponse(
                    t.ticketId,
                    t.serialNumber,
                    t.status,
                    t.title,
                    t.category.parent.name,
                    t.category.name,
                    t.user.nickname,
                    t.manager.nickname,
                    t.createdAt,
                    t.updatedAt
                )
            )
            .from(t)
            .where(
                titleOrManagerNicknameOrSerialNumberContainsIgnoreCase(query),
                statusEq(status),
                createdAtGoe(startDate),
                createdAtLoe(endDate),
                isManagerAccessible()
            )
            .orderBy(t.ticketId.desc())
            .fetch();
    }

    @Override
    public Page<TicketEntity> findByManagerFilters(
        Long managerId,
        TicketStatus status,
        ApplicationPageRequest pageRequest,
        String query
    ) {
        var orderSpecifiers = getOrderSpecifier(pageRequest.sortType());

        List<TicketEntity> ticketEntityList = jpaQueryFactory.selectFrom(ticketEntity)
            .where(
                ticketEntity.manager.memberId.eq(managerId),
                statusEq(status),
                searchEq(query),
                isManagerAccessible()
            )
            .orderBy(orderSpecifiers)
            .offset((long) pageRequest.size() * pageRequest.page())
            .limit(pageRequest.size())
            .fetch();

        JPAQuery<Long> total = jpaQueryFactory.select(ticketEntity.count())
            .from(ticketEntity)
            .where(
                ticketEntity.manager.memberId.eq(managerId),
                statusEq(status),
                searchEq(query),
                isManagerAccessible()
            );

        return PageableExecutionUtils.getPage(
            ticketEntityList,
            pageRequest.toPageable(),
            total::fetchOne
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

    private OrderSpecifier<?>[] getOrderSpecifier(SortType sortType) {
        ArrayList<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        orderSpecifiers.add(ticketEntity.isPinned.desc());
        if (sortType != null) {
            orderSpecifiers.add(
                switch (sortType) {
                    case NEWEST -> ticketEntity.createdAt.desc();
                    case OLDEST -> ticketEntity.createdAt.asc();
                    case UPDATED -> ticketEntity.updatedAt.desc();
                }
            );
        }
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression searchEq(String search) {
        return search == null ? null : ticketEntity.serialNumber.contains(search)
            .or(ticketEntity.content.contains(search));
    }

    private BooleanExpression isManagerAccessible() {
        return ticketEntity.status.ne(TicketStatus.CANCEL);
    }
}
