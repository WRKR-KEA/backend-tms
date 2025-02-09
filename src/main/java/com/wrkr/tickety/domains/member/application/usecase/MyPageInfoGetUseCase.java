package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MyPageMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageInfoGetUseCase {

    private final MemberGetService memberGetService;

    public MyPageInfoResponse getMyPageInfo(Long memberId) {
        Member findMember = memberGetService.byMemberId(memberId);

        if (findMember.isDeleted()) {
            throw ApplicationException.from(MemberErrorCode.DELETED_MEMBER);
        }

        return MyPageMapper.toMyPageInfoResponse(findMember);
    }
}
