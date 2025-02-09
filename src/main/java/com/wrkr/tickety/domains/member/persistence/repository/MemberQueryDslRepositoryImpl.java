package com.wrkr.tickety.domains.member.persistence.repository;

import static com.wrkr.tickety.domains.member.persistence.entity.QMemberEntity.memberEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberQueryDslRepositoryImpl implements MemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // TODO: like 쿼리 사용하지 않도록 개선, No offset 페이징도 가능할지 생각해보기, Projection 기반으로 리팩토링
    @Override
    public Page<MemberEntity> searchMember(Role role, String query, Pageable pageable) {

        List<MemberEntity> memberEntityList = jpaQueryFactory
            .selectFrom(memberEntity)
            .where(
                roleEq(role),
                emailOrNameOrDepartmentContainsIgnoreCase(query),
                isDeletedEq(false)
            )
            .orderBy(memberEntity.memberId.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(memberEntity.count())
            .from(memberEntity)
            .where(
                roleEq(role),
                emailOrNameOrDepartmentContainsIgnoreCase(query),
                isDeletedEq(false)
            );

        return PageableExecutionUtils.getPage(
            memberEntityList,
            pageable,
            countQuery::fetchOne
        );
    }

    private BooleanExpression roleEq(Role role) {
        return role == null ? null : memberEntity.role.eq(role);
    }

    private BooleanExpression emailOrNameOrDepartmentContainsIgnoreCase(String query) {
        return query == null || query.isBlank() ? null : memberEntity.email.containsIgnoreCase(query)
            .or(memberEntity.name.containsIgnoreCase(query))
            .or(memberEntity.department.containsIgnoreCase(query));
    }

    private BooleanExpression isDeletedEq(Boolean isDeleted) {
        return isDeleted == null ? null : memberEntity.isDeleted.eq(isDeleted);
    }
}
