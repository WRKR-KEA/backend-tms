package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.auth.utils.PasswordEncoderUtil;
import com.wrkr.tickety.domains.member.application.dto.request.EmailCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberSaveService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.UUIDGenerator;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class MemberCreateUseCase {

    private final MemberSaveService memberSaveService;
    private final EmailUtil emailUtil;

    public MemberPkResponse createMember(MemberCreateRequest memberCreateRequest) {
        String tempPassword = UUIDGenerator.generateUUID().substring(0, 12);

        String encryptedPassword = PasswordEncoderUtil.encodePassword(tempPassword);
        Member createdMember = memberSaveService.save(MemberMapper.toMember(memberCreateRequest, encryptedPassword));

        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(createdMember.getEmail(), EmailConstants.TEMP_PASSWORD_SUBJECT, null);
        emailUtil.sendMail(emailCreateRequest, tempPassword, EmailConstants.TYPE_PASSWORD);

        return MemberMapper.toMemberPkResponse(PkCrypto.encrypt(createdMember.getMemberId()));
    }
}
