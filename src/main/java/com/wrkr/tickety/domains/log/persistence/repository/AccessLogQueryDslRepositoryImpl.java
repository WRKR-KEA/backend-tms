package com.wrkr.tickety.domains.log.persistence.repository;

import static com.wrkr.tickety.domains.log.persistence.entity.QAccessLogEntity.accessLogEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.persistence.entity.AccessLogEntity;
import com.wrkr.tickety.domains.log.persistence.entity.QAccessLogEntity;
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
public class AccessLogQueryDslRepositoryImpl implements AccessLogQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<AccessLogEntity> searchAccessLogs(Pageable pageable, String query, ActionType action, LocalDate startDate, LocalDate endDate) {
        List<AccessLogEntity> accessLogEntities = jpaQueryFactory
            .selectFrom(accessLogEntity)
            .where(
                nicknameOrIpContainsIgnoreCase(query),
                actionEq(action),
                accessAtGoe(startDate),
                accessAtLoe(endDate)
            )
            .orderBy(accessLogEntity.accessLogId.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(accessLogEntity.count())
            .from(accessLogEntity)
            .where(
                nicknameOrIpContainsIgnoreCase(query),
                actionEq(action),
                accessAtGoe(startDate),
                accessAtLoe(endDate)
            );

        return PageableExecutionUtils.getPage(
            accessLogEntities,
            pageable,
            countQuery::fetchOne
        );
    }

    @Override
    public List<AccessLogEntity> findAllAccessLogs(String query, ActionType action, LocalDate startDate, LocalDate endDate) {
        QAccessLogEntity al = accessLogEntity;

        return jpaQueryFactory
            .selectFrom(al)
            .where(
                nicknameOrIpContainsIgnoreCase(query),
                actionEq(action),
                accessAtGoe(startDate),
                accessAtLoe(endDate)
            )
            .orderBy(accessLogEntity.accessLogId.desc())
            .fetch();
    }

    private BooleanExpression nicknameOrIpContainsIgnoreCase(String query) {
        return query == null || query.isBlank() ?
            null :
            accessLogEntity.nickname.containsIgnoreCase(query)
                .or(accessLogEntity.ip.containsIgnoreCase(query));
    }

    private BooleanExpression actionEq(ActionType action) {
        return action == null ? null : accessLogEntity.action.eq(action);
    }

    private BooleanExpression accessAtGoe(LocalDate startDate) {
        return startDate == null ? null : accessLogEntity.accessAt.goe(LocalDateTime.of(startDate, LocalTime.MIN));
    }

    private BooleanExpression accessAtLoe(LocalDate endDate) {
        return endDate == null ? null : accessLogEntity.accessAt.loe(LocalDateTime.of(endDate, LocalTime.MAX));
    }
}
