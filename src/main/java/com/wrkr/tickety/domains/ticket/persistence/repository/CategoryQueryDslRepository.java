package com.wrkr.tickety.domains.ticket.persistence.repository;

import java.util.List;

public interface CategoryQueryDslRepository {

    public List<Long> findChildIdsByParentCategoryIdAndIsDeletedFalse(Long categoryId);
}
