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

    // TODO: like 쿼리 사용하지 않도록 개선
    @Override
    public Page<MemberEntity> searchMember(Pageable pageable, Role role, String email, String name, String department) {

        List<MemberEntity> memberEntityList = jpaQueryFactory
            .selectFrom(memberEntity)
            .where(
                roleEq(role),
                containsEmailIgnoreCase(email),
                containsNameIgnoreCase(name),
                containsDepartmentIgnoreCase(department)
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
                containsEmailIgnoreCase(email),
                containsNameIgnoreCase(name),
                containsDepartmentIgnoreCase(department)
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

    private BooleanExpression containsEmailIgnoreCase(String email) {
        return email == null || email.isEmpty() ? null : memberEntity.email.containsIgnoreCase(email);
    }

    private BooleanExpression containsNameIgnoreCase(String name) {
        return name == null || name.isEmpty() ? null : memberEntity.name.containsIgnoreCase(name);
    }

    private BooleanExpression containsDepartmentIgnoreCase(String department) {
        return department == null || department.isEmpty() ? null : memberEntity.department.containsIgnoreCase(department);
    }
}
