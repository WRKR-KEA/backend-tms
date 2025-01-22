package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.request.MemberUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberUpdateService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
@Transactional
public class MemberUpdateUseCase {
    private final MemberUpdateService memberUpdateService;
    private final MemberGetService memberGetService;

    public MemberPkResponse modifyMemberInfo(String memberId, MemberUpdateRequest memberUpdateRequest) {
        Member findMember = memberGetService.ByMemberId(PkCrypto.decrypt(memberId))
                .orElseThrow(() -> new ApplicationException(MemberErrorCode.MEMBER_NOT_FOUND));

        findMember.modifyMemberInfo(memberUpdateRequest);
        Member modifiedMember = memberUpdateService.modifyMemberInfo(findMember);

        return MemberMapper.toMemberPkResponse(PkCrypto.encrypt(modifiedMember.getMemberId()));
    }

    public void softDeleteMember(List<String> memberIdList) {
        memberIdList.forEach(memberId -> {
            Member findMember = memberGetService.ByMemberId(PkCrypto.decrypt(memberId))
                    .orElseThrow(() -> new ApplicationException(MemberErrorCode.MEMBER_NOT_FOUND));

            findMember.modifyIsDeleted(true);
            Member modifiedMember = memberUpdateService.modifyMemberInfo(findMember);

            memberUpdateService.modifyMemberInfo(modifiedMember);
        });
    }
}
