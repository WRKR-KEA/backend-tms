package com.wrkr.tickety.domains.member.persistence.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.wrkr.tickety.domains.member.persistence.entity.QMemberEntity.memberEntity;

@Repository
@RequiredArgsConstructor
public class MemberQueryDslRepositoryImpl implements MemberQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<MemberEntity> pagingByRole(Pageable pageable, Role role) {

        long totalCount = jpaQueryFactory
                .select(memberEntity.count())
                .from(memberEntity)
                .where(roleEq(role))
                .fetchOne();

        List<MemberEntity> members = jpaQueryFactory
                .selectFrom(memberEntity)
                .where(roleEq(role))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(members, pageable, totalCount);
    }

    private BooleanExpression roleEq(Role role) {
        return role == null ? null : memberEntity.role.eq(role);
    }
}
