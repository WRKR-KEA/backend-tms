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
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
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
        StatisticsType statisticsType,
        TicketStatus ticketStatus
    ) {
        QTicketHistoryEntity th = ticketHistoryEntity;

        StringTemplate dateFormat = getDateFormatTemplate(statisticsType, th);

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
                statusEq(ticketStatus)
            )
            .groupBy(dateFormat)
            .orderBy(dateFormat.asc())
            .fetch();

        return countList;
    }

    private StringTemplate getDateFormatTemplate(StatisticsType statisticsType, QTicketHistoryEntity th) {
        return switch (statisticsType) {
            case YEARLY -> Expressions.stringTemplate("DATE_FORMAT({0}, {1})", th.createdAt, ConstantImpl.create("%Y"));
            case MONTHLY -> Expressions.stringTemplate("DATE_FORMAT({0}, {1})", th.createdAt, ConstantImpl.create("%Y-%m"));
            case DAILY -> Expressions.stringTemplate("DATE_FORMAT({0}, {1})", th.createdAt, ConstantImpl.create("%Y-%m-%d"));
            case HOURLY -> Expressions.stringTemplate("DATE_FORMAT({0}, {1})", th.createdAt, ConstantImpl.create("%Y-%m-%d %H"));
            default -> throw new ApplicationException(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID);
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

