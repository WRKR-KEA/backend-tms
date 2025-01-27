package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.request.MyPageInfoUpdateRequest;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberUpdateService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class MyPageInfoUpdateUseCase {

    private final MemberGetService memberGetService;
    private final MemberUpdateService memberUpdateService;

    public PkResponse updateMyPageInfo(Long memberId, MyPageInfoUpdateRequest request) {
        Optional<Member> member = memberGetService.byMemberId(memberId);

        if (member.get().isDeleted()) {
            throw ApplicationException.from(MemberErrorCode.DELETED_MEMBER);
        }

        member.get().modifyMyPageInfo(request.position(), request.phone());
        Member modifieddMember = memberUpdateService.modifyMemberInfo(member.get());

        return PkResponse.builder()
            .id(PkCrypto.encrypt(modifieddMember.getMemberId()))
            .build();
    }
}
