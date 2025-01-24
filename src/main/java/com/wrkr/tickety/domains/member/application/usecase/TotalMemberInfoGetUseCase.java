package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoPagingResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TotalMemberInfoGetUseCase {

    private final MemberGetService memberGetService;

    public MemberInfoPagingResponse getTotalMemberInfo(int page, int size, String role) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Member> memberPage = memberGetService.pagingByRole(pageable, Role.from(role));
        List<MemberInfoResponse> memberInfoResponseList = MemberMapper.toMemberInfoResponseList(memberPage.getContent());

        return MemberMapper.toMemberInfoPagingResponse(
            memberInfoResponseList,
            memberPage.getNumber() + 1,
            memberPage.getTotalPages(),
            memberPage.getTotalElements(),
            memberPage.getSize()
        );
    }
}
