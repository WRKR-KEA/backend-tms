package com.wrkr.tickety.domains.ticket.persistence.repository;

import static com.wrkr.tickety.domains.ticket.persistence.entity.QTicketHistoryEntity.ticketHistoryEntity;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.QStatisticsByTicketStatusResponse_TicketCount;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse.TicketCount;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.QTicketHistoryEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketHistoryQueryDslRepositoryImpl implements TicketHistoryQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<TicketCount> countByTicketStatusDuringPeriod(
        LocalDateTime startDate,
        LocalDateTime endDate,
        StatisticsType type,
        TicketStatus status
    ) {
        QTicketHistoryEntity th = ticketHistoryEntity;

        StringTemplate dateFormat = getDateFormatTemplate(type, th);

        List<TicketCount> countList = jpaQueryFactory
            .select(
                new QStatisticsByTicketStatusResponse_TicketCount(
                    dateFormat,
                    th.ticketHistoryId.count()
                )
            )
            .from(th)
            .where(
                betweenPeriod(startDate, endDate),
                statusEq(status)
            )
            .groupBy(dateFormat)
            .orderBy(dateFormat.asc())
            .fetch();

        return countList;
    }

    private StringTemplate getDateFormatTemplate(StatisticsType statisticsType, QTicketHistoryEntity th) {
        return switch (statisticsType) {
            case YEARLY -> Expressions.stringTemplate("DATE_FORMAT({0}, {1})", th.createdAt, ConstantImpl.create("%Y-%m"));
            case MONTHLY -> Expressions.stringTemplate("DATE_FORMAT({0}, {1})", th.createdAt, ConstantImpl.create("%Y-%m-%d"));
            case DAILY -> Expressions.stringTemplate("DATE_FORMAT({0}, {1})", th.createdAt, ConstantImpl.create("%Y-%m-%d %H"));
            case TOTAL -> Expressions.stringTemplate("DATE_FORMAT({0}, {1})", th.createdAt, ConstantImpl.create("%Y"));
        };
    }

    private BooleanExpression statusEq(TicketStatus status) {
        return status == null ? null : ticketHistoryEntity.status.eq(status);
    }

    private BooleanExpression betweenPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        // datetime의 최솟값: 1000-01-01 00:00:00.000000
        return startDate == null || endDate == null || startDate.getYear() == 1000 ? null : ticketHistoryEntity.createdAt.between(startDate, endDate);
    }
}

