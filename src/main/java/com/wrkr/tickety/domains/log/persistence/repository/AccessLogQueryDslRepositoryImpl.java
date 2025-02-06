package com.wrkr.tickety.domains.log.persistence.repository;

import static com.wrkr.tickety.domains.log.persistence.entity.QAccessLogEntity.accessLogEntity;
import static com.wrkr.tickety.domains.member.persistence.entity.QMemberEntity.memberEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.persistence.entity.AccessLogEntity;
import com.wrkr.tickety.domains.member.domain.constant.Role;
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
    public Page<AccessLogEntity> searchAccessLogs(Pageable pageable, Role role, String query, ActionType action) {
        List<AccessLogEntity> accessLogEntities = jpaQueryFactory
            .selectFrom(accessLogEntity)
            .where(
                roleEq(role),
                nicknameOrIpContainsIgnoreCase(query),
                actionEq(action)
            )
            .orderBy(accessLogEntity.accessLogId.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(accessLogEntity.count())
            .from(accessLogEntity)
            .where(
                roleEq(role),
                nicknameOrIpContainsIgnoreCase(query),
                actionEq(action)
            );

        return PageableExecutionUtils.getPage(
            accessLogEntities,
            pageable,
            countQuery::fetchOne
        );
    }

    private BooleanExpression roleEq(Role role) {
        return role == null ? null : memberEntity.role.eq(role);
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
}
