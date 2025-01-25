package com.wrkr.tickety.domains.member.persistence.repository;

import static com.wrkr.tickety.domains.member.persistence.entity.QMemberEntity.memberEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberQueryDslRepositoryImpl implements MemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // TODO: like 쿼리 사용하지 않도록 개선
    @Override
    public Page<MemberEntity> searchMember(Pageable pageable, Role role, String email, String name, String department) {

        // TODO: 첫 페이지이면서, 전체 Content 사이즈가 PageSize보다 작은 경우 Counting 쿼리가 실행되지 않도록 최적화 필요
        long totalCount = jpaQueryFactory
            .select(memberEntity.count())
            .from(memberEntity)
            .where(
                roleEq(role),
                containsEmailIgnoreCase(email),
                containsNameIgnoreCase(name),
                containsDepartmentIgnoreCase(department)
            ).fetchOne();

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

        return new PageImpl<>(memberEntityList, pageable, totalCount);
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
