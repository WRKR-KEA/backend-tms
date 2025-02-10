package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.RandomCodeGenerator;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import com.wrkr.tickety.infrastructure.email.EmailUtil;
import com.wrkr.tickety.infrastructure.redis.RedisService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@UseCase
@RequiredArgsConstructor
@Transactional
public class VerificationCodeCreateUseCase {

    private static final String VERIFICATION_CODE_PREFIX = "verification-code-";

    private final MemberGetService memberGetService;
    private final RedisService redisService;
    private final EmailUtil emailUtil;

    public MemberPkResponse createVerificationCode(String nickname) {
        Member findMember = memberGetService.loadMemberByNickname(nickname);

        // 인증 코드 이미 존재하면 삭제
        String encryptedMemberId = PkCrypto.encrypt(findMember.getMemberId());
        redisService.deleteValues(VERIFICATION_CODE_PREFIX + encryptedMemberId);

        String randomCode = RandomCodeGenerator.generateRandomCode();
        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(findMember.getEmail(), EmailConstants.VERIFICATION_CODE_SUBJECT, null);

        redisService.setValues(VERIFICATION_CODE_PREFIX + encryptedMemberId, randomCode, Duration.ofMinutes(5));

        emailUtil.sendMail(emailCreateRequest, randomCode, EmailConstants.FILENAME_VERIFICATION_CODE);

        // TODO: 릴리즈 하기 전에 삭제
        log.info("**** 인증 번호 생성 ****");
        log.info("아이디: {}, 인증 번호 : {}", findMember.getNickname(), randomCode);

        return MemberMapper.toMemberPkResponse(encryptedMemberId);
    }
}
