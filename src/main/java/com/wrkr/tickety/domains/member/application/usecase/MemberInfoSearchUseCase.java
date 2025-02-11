package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoSearchUseCase {

    private final MemberGetService memberGetService;

    public ApplicationPageResponse<MemberInfoResponse> searchMemberInfo(
        ApplicationPageRequest pageRequest,
        Role role,
        String query
    ) {
        validateRole(role);

        Page<Member> memberPage = memberGetService.searchMember(
            pageRequest,
            role,
            query
        );

        return ApplicationPageResponse.of(
            memberPage,
            MemberMapper::toMemberInfoResponse
        );
    }

    private void validateRole(Role role) {
        if (role.equals(Role.ADMIN)) {
            throw ApplicationException.from(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
    }
}
