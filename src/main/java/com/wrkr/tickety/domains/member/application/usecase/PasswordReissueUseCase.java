package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.auth.utils.PasswordEncoderUtil;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberSaveService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.UUIDGenerator;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import com.wrkr.tickety.infrastructure.email.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class PasswordReissueUseCase {

    private final MemberGetService memberGetService;
    private final MemberSaveService memberSaveService;
    private final EmailUtil emailUtil;

    public MemberPkResponse reissuePassword(String nickname) {
        Member findMember = memberGetService.loadMemberByNickname(nickname);

        String tempPassword = UUIDGenerator.generateUUID().substring(0, 12);
        String encryptedPassword = PasswordEncoderUtil.encodePassword(tempPassword);

        findMember.modifyPassword(
            encryptedPassword,
            true
        );

        Member modifiedMember = memberSaveService.save(findMember);

        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(findMember.getEmail(), EmailConstants.REISSUE_PASSWORD_SUBJECT, null);
        emailUtil.sendMail(emailCreateRequest, tempPassword, EmailConstants.TYPE_PASSWORD);

        return MemberMapper.toMemberPkResponse(PkCrypto.encrypt(modifiedMember.getMemberId()));
    }
}
