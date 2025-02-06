package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.response.ManagerGetAllManagerResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ManagerGetAllManagersUseCase {

    private final MemberGetService memberGetService;
    private final TicketGetService ticketGetService;

    public ManagerGetAllManagerResponse getAllManagers() {
        List<Member> managers = memberGetService.getAllManagers();
        List<Long> managerIds = managers.stream().map(Member::getMemberId).toList();

        List<Ticket> inProgressTickets = ticketGetService.getManagersInProgressTickets(managerIds);
        Map<Long, Long> inProgressTicketCount = getTicketInProgressCount(inProgressTickets);
        return MemberMapper.toManagerGetAllManagerResponse(memberGetService.getAllManagers(), inProgressTicketCount);
    }

    public ApplicationPageResponse<ManagerGetAllManagerResponse.Managers> getAllPageManagers(ApplicationPageRequest pageRequest) {
        Page<Member> managerPage = memberGetService.getAllManagersPage(pageRequest);
        List<Member> managers = managerPage.getContent();

        List<Long> managerIds = managers.stream()
            .map(Member::getMemberId)
            .toList();

        List<Ticket> inProgressTickets = ticketGetService.getManagersInProgressTickets(managerIds);
        Map<Long, Long> inProgressTicketCount = getTicketInProgressCount(inProgressTickets);

        return ApplicationPageResponse.of(
            managerPage,
            member -> MemberMapper.toManagerResponse(member, inProgressTicketCount)
        );
    }

    private Map<Long, Long> getTicketInProgressCount(List<Ticket> inProgressTickets) {
        return inProgressTickets.stream()
            .collect(Collectors.groupingBy(
                ticket -> ticket.getManager().getMemberId(),
                Collectors.counting()
            ));
    }
}
