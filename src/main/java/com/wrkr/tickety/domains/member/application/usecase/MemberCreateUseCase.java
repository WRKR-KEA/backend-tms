package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.request.EmailCreateReqDTO;
import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateReqDTO;
import com.wrkr.tickety.domains.member.application.dto.response.MemberCreateResDTO;
import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.constant.EmailConstants;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberCreateService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class MemberCreateUseCase {
    private final MemberCreateService memberCreateService;
    private final EmailCreateUseCase emailCreateUseCase;

    public MemberCreateResDTO createMember(MemberCreateReqDTO reqDTO) {
        String tempPassword = UUIDGenerator.generateUUID().substring(0, 12);
        String encryptedPassword = PkCrypto.encrypt(tempPassword);
        Member createdMember = memberCreateService.createMember(MemberMapper.toMember(reqDTO, encryptedPassword));

        EmailCreateReqDTO emailCreateReqDTO = EmailMapper.toEmailCreateReqDTO(createdMember.getEmail(), EmailConstants.TEMP_PASSWORD_SUBJECT, null);
        emailCreateUseCase.sendMail(emailCreateReqDTO, tempPassword, EmailConstants.TYPE_PASSWORD);

        return MemberMapper.toMemberCreateResDTO(PkCrypto.encrypt(createdMember.getMemberId()));
    }
}
