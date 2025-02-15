package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.auth.utils.PasswordEncoderUtil;
import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberSaveService;
import com.wrkr.tickety.domains.member.presentation.util.validator.MemberFieldValidator;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.RandomCodeGenerator;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import com.wrkr.tickety.infrastructure.email.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@UseCase
@RequiredArgsConstructor
@Transactional
public class MemberCreateUseCase {

    private static final String DEFAULT_PROFILE_IMAGE_URL = "https://i.ibb.co/7Fd4Hhx/tickety-default-image.jpg";

    private final MemberSaveService memberSaveService;
    private final S3ApiService s3ApiService;
    private final EmailUtil emailUtil;
    private final MemberFieldValidator memberFieldValidator;

    public MemberPkResponse createMember(MemberCreateRequest memberCreateRequest, MultipartFile profileImage) {
        memberFieldValidator.validateEmailDuplicate(memberCreateRequest.email());
        memberFieldValidator.validateNicknameDuplicate(memberCreateRequest.nickname());

        String tempPassword = RandomCodeGenerator.generateUUID().substring(0, 12);
        String encryptedPassword = PasswordEncoderUtil.encodePassword(tempPassword);

        String profileImageUrl =
            (profileImage == null || profileImage.isEmpty()) ? DEFAULT_PROFILE_IMAGE_URL : s3ApiService.uploadMemberProfileImage(profileImage);

        String agiturl = memberCreateRequest.agitUrl();
        Member createdMember = memberSaveService.save(
            MemberMapper.mapToMember(memberCreateRequest, encryptedPassword, profileImageUrl, agiturl));

        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(
            createdMember.getEmail(),
            EmailConstants.TEMP_PASSWORD_SUBJECT,
            null
        );
        emailUtil.sendMail(emailCreateRequest, tempPassword, EmailConstants.FILENAME_PASSWORD);

        // TODO: 릴리즈 하기 전에 삭제
        log.info("**** 회원 등록 됨 ****");
        log.info("이메일 : {}, 임시 비밀번호 : {}", createdMember.getEmail(), tempPassword);

        return MemberMapper.toMemberPkResponse(PkCrypto.encrypt(createdMember.getMemberId()));
    }
}
