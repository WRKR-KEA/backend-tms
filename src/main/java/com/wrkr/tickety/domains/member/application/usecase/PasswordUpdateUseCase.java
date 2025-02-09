package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.auth.utils.PasswordEncoderUtil;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberSaveService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class PasswordUpdateUseCase {

    private final MemberGetService memberGetService;
    private final MemberSaveService memberSaveService;

    public MemberPkResponse updatePassword(Long memberId, String password, String confirmPassword) {
        validatePassword(password, confirmPassword);

        Member findMember = memberGetService.byMemberId(memberId);

        findMember.modifyPassword(
            PasswordEncoderUtil.encodePassword(password),
            false
        );

        Member modifiedMember = memberSaveService.save(findMember);

        return MemberMapper.toMemberPkResponse(PkCrypto.encrypt(modifiedMember.getMemberId()));
    }

    private static void validatePassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw ApplicationException.from(MemberErrorCode.UNMATCHED_PASSWORD);
        }
    }
}
