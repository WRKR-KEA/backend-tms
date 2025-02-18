package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.auth.exception.AuthErrorCode;
import com.wrkr.tickety.domains.auth.utils.PasswordEncoderUtil;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberSaveService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.RandomCodeGenerator;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import com.wrkr.tickety.infrastructure.email.EmailUtil;
import com.wrkr.tickety.infrastructure.redis.RedisService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@UseCase
@RequiredArgsConstructor
@Transactional
public class PasswordReissueUseCase {

    private static final String VERIFICATION_CODE_PREFIX = "verification-code-";

    private final MemberGetService memberGetService;
    private final MemberSaveService memberSaveService;
    private final RedisService redisService;
    private final EmailUtil emailUtil;

    public MemberPkResponse reissuePassword(String encryptedMemberId, String verificationCodeReq) {
        Optional<String> findVerificationCode = redisService.getValues(VERIFICATION_CODE_PREFIX + encryptedMemberId);
        validateVerificationCode(verificationCodeReq, findVerificationCode);

        Long decryptedMemberId = PkCrypto.decrypt(encryptedMemberId);
        Member findMember = memberGetService.byMemberId(decryptedMemberId);

        String tempPassword = RandomCodeGenerator.generateUUID().substring(0, 12);
        String encryptedPassword = PasswordEncoderUtil.encodePassword(tempPassword);

        findMember.modifyPassword(
            encryptedPassword,
            true
        );

        Member modifiedMember = memberSaveService.save(findMember);

        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(findMember.getEmail(), EmailConstants.REISSUE_PASSWORD_SUBJECT, null);
        emailUtil.sendMail(emailCreateRequest, tempPassword, EmailConstants.FILENAME_PASSWORD);

        redisService.deleteValues(VERIFICATION_CODE_PREFIX + decryptedMemberId);

        // TODO: 릴리즈 하기 전에 삭제
        log.info("**** 재발급된 비밀번호 ****");
        log.info("아이디: {}, 비밀번호 : {}", findMember.getNickname(), tempPassword);

        return MemberMapper.toMemberPkResponse(PkCrypto.encrypt(modifiedMember.getMemberId()));
    }

    private void validateVerificationCode(String verificationCodeReq, Optional<String> findVerificationCode) {
        if (findVerificationCode.isEmpty() || !verificationCodeReq.equals(findVerificationCode.get())) {
            throw ApplicationException.from(AuthErrorCode.INVALID_VERIFICATION_CODE);
        }
    }

}
