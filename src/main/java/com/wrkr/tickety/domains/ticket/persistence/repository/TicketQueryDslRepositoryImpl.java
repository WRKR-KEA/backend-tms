package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketQueryDslRepositoryImpl implements TicketQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;
}
