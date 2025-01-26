package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MyPageMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageInfoGetUseCase {

    private final MemberGetService memberGetService;

    public MyPageInfoResponse getMyPageInfo(String memberId) {
        Optional<Member> findMember = memberGetService.byMemberId(PkCrypto.decrypt(memberId));

        if (findMember.get().IsDeleted()) {
            throw ApplicationException.from(MemberErrorCode.DELETED_MEMBER);
        }

        return MyPageMapper.toMyPageInfoResposne(findMember.orElse(null));
    }
}
