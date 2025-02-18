package com.wrkr.tickety.domains.ticket.persistence.repository;

import static com.wrkr.tickety.domains.ticket.persistence.entity.QCategoryEntity.categoryEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryQueryDslRepositoryImpl implements CategoryQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Long> findChildIdsByParentCategoryIdAndIsDeletedFalse(Long categoryId) {

        return jpaQueryFactory.select(categoryEntity.categoryId)
            .from(categoryEntity)
            .where(
                parentCategoryIdEq(categoryId)
                    .and(isDeletedFalse())
                  )
            .fetch();
    }

    private BooleanExpression isDeletedFalse() {
        return categoryEntity.isDeleted.isFalse();
    }

    private BooleanExpression parentCategoryIdEq(Long categoryId) {
        return categoryEntity.parent.categoryId.eq(categoryId);
    }
}
