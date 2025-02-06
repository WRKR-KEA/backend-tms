package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.auth.utils.PasswordEncoderUtil;
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
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import com.wrkr.tickety.infrastructure.email.EmailUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberCreateUseCase {

    private final MemberSaveService memberSaveService;
    private final EmailUtil emailUtil;

    public List<MemberPkResponse> createMember(List<MemberCreateRequest> memberCreateRequests) {
        List<MemberPkResponse> memberPkResponses = new ArrayList<>();

        for (MemberCreateRequest memberCreateRequest : memberCreateRequests) {
            String tempPassword = UUIDGenerator.generateUUID().substring(0, 12);
            String encryptedPassword = PasswordEncoderUtil.encodePassword(tempPassword);

            Member createdMember = memberSaveService.save(MemberMapper.toMember(memberCreateRequest, encryptedPassword));

            EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(
                createdMember.getEmail(),
                EmailConstants.TEMP_PASSWORD_SUBJECT,
                null
            );
            emailUtil.sendMail(emailCreateRequest, tempPassword, EmailConstants.TYPE_PASSWORD);

            log.info("**** 회원 등록 됨 ****");
            log.info("이메일 : {}, 임시 비밀번호 : {}", createdMember.getEmail(), tempPassword);

            MemberPkResponse memberPkResponse = MemberMapper.toMemberPkResponse(PkCrypto.encrypt(createdMember.getMemberId()));

            memberPkResponses.add(memberPkResponse);
        }

        return memberPkResponses;
    }
}
