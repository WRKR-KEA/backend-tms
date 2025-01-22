package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.request.EmailCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.constant.EmailConstants;
import com.wrkr.tickety.domains.member.persistence.entity.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberCreateService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class MemberCreateUseCase {
    private final MemberCreateService memberCreateService;
    private final EmailCreateUseCase emailCreateUseCase; // TODO: UseCase간 서로 참조하도록 해도 되는지 고민 필요

    public MemberPkResponse createMember(MemberCreateRequest reqDTO) {
        String tempPassword = UUIDGenerator.generateUUID().substring(0, 12);
        String encryptedPassword = PkCrypto.encrypt(tempPassword);
        Member createdMember = memberCreateService.createMember(MemberMapper.toMember(reqDTO, encryptedPassword));

        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateReqDTO(createdMember.getEmail(), EmailConstants.TEMP_PASSWORD_SUBJECT, null);
        emailCreateUseCase.sendMail(emailCreateRequest, tempPassword, EmailConstants.TYPE_PASSWORD);

        return MemberMapper.toMemberPkResponse(PkCrypto.encrypt(createdMember.getMemberId()));
    }
}
