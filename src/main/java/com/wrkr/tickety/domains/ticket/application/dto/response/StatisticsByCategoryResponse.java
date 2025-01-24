package com.wrkr.tickety.domains.ticket.application.dto.response;

import java.util.List;

public record StatisticsByCategoryResponse(
    String date,
    String message,
    Result result
) {

    public record Result(
        List<FirstCategory> firstCategory,
        List<SecondCategory> secondCategory
    ) {

    }

    public record FirstCategory(
        String categoryId,
        String categoryName,
        int count
    ) {

    }

    public record SecondCategory(
        String categoryId,
        String categoryName,
        int count
    ) {

    }
}

