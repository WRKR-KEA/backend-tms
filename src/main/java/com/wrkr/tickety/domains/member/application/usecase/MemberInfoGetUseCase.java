package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoGetUseCase {

    private final MemberGetService memberGetService;

    public MemberInfoResponse getMemberInfo(String memberId) {
        // TODO: 프로젝션 사용도 고려
        Member findMember = memberGetService.byMemberId(PkCrypto.decrypt(memberId));

        return MemberMapper.mapToMemberInfoResponse(findMember);
    }
}
