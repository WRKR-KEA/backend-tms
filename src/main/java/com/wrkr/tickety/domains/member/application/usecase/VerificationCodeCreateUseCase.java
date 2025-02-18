package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.member.presentation.util.validator.MemberFieldValidator;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.RandomCodeGenerator;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import com.wrkr.tickety.infrastructure.email.EmailUtil;
import com.wrkr.tickety.infrastructure.redis.RedisService;
import java.time.Duration;
import java.util.Optional;
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
    private final MemberFieldValidator memberFieldValidator;

    public MemberPkResponse createVerificationCode(String nickname) {
        memberFieldValidator.validateNicknameFormat(nickname);

        Optional<Member> findMember = memberGetService.findMemberByNickname(nickname);

        validateMemberExists(findMember);

        // 인증 코드 이미 존재하면 삭제
        String encryptedMemberId = PkCrypto.encrypt(findMember.get().getMemberId());
        redisService.deleteValues(VERIFICATION_CODE_PREFIX + encryptedMemberId);

        String randomCode = RandomCodeGenerator.generateRandomCode();
        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(findMember.get().getEmail(), EmailConstants.VERIFICATION_CODE_SUBJECT, null);

        redisService.setValues(VERIFICATION_CODE_PREFIX + encryptedMemberId, randomCode, Duration.ofMinutes(5));

        emailUtil.sendMail(emailCreateRequest, randomCode, EmailConstants.FILENAME_VERIFICATION_CODE);

        return MemberMapper.toMemberPkResponse(encryptedMemberId);
    }

    // 회원이 존재하지 않는 경우도 에러 코드 통일
    private static void validateMemberExists(Optional<Member> findMember) {
        if (findMember.isEmpty()) {
            throw ApplicationException.from(MemberErrorCode.INVALID_NICKNAME);
        }
    }
}
