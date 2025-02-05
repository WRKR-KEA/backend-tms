package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoSearchUseCase {

    private final MemberGetService memberGetService;

    public PageResponse<MemberInfoResponse> searchMemberInfo(
        Role role,
        String query,
        Pageable pageable
    ) {
        Page<Member> memberPage = memberGetService.searchMember(
            role,
            query,
            pageable
        );

        return PageResponse.of(
            memberPage,
            MemberMapper::toMemberInfoResponse
        );
    }
}
