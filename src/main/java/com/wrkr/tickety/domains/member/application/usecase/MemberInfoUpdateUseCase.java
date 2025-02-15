package com.wrkr.tickety.domains.member.application.usecase;

import static com.wrkr.tickety.global.response.code.CommonErrorCode.EXCEED_MAX_FILE_SIZE;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.INVALID_IMAGE_EXTENSION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.auth.exception.AuthErrorCode;
import com.wrkr.tickety.domains.member.application.dto.request.MemberInfoUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.application.mapper.MyPageMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberUpdateService;
import com.wrkr.tickety.domains.member.presentation.util.validator.MemberFieldValidator;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.infrastructure.redis.RedisService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@UseCase
@RequiredArgsConstructor
@Transactional
public class MemberInfoUpdateUseCase {

    private static final String[] ACCEPTED_EXTENSIONS = {"jpg", "jpeg", "png"};
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final MemberUpdateService memberUpdateService;
    private final MemberGetService memberGetService;
    private final S3ApiService s3ApiService;
    private final MemberFieldValidator memberFieldValidator;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public MemberPkResponse modifyMemberInfo(String memberId, MemberInfoUpdateRequest request, MultipartFile profileImage) {
        if (isFileUpload(profileImage)) {
            validateFileSize(profileImage);
            validateImageFileExtension(profileImage);
        }

        Member findMember = memberGetService.byMemberId(PkCrypto.decrypt(memberId));

        validateAuthorization(findMember);
        validateEmailDuplicate(findMember.getEmail(), request.email());
        validateNicknameDuplicate(findMember.getNickname(), request.nickname());

        boolean isEmptyProfileImage = profileImage == null || profileImage.isEmpty();

        if (!isEmptyProfileImage) {
            s3ApiService.deleteObject(findMember.getProfileImage());
            String newProfileImageUrl = s3ApiService.uploadMemberProfileImage(profileImage);

            findMember.modifyProfileImage(newProfileImageUrl);
        }

        String agitUrl = request.agitUrl();
        findMember.modifyMemberInfo(request, agitUrl);
        Member modifiedMember = memberUpdateService.modifyMemberInfo(findMember);

        String key = "MEMBER_INFO:" + modifiedMember.getMemberId();
        redisService.deleteValues(key);

        try {
            MyPageInfoResponse updatedResponse = MyPageMapper.toMyPageInfoResponse(modifiedMember);
            String jsonData = objectMapper.writeValueAsString(updatedResponse);
            redisService.setValuesWithoutTTL(key, jsonData);
        } catch (Exception e) {
            throw new RuntimeException("회원 정보 캐싱 중 오류 발생", e);
        }

        return MemberMapper.toMemberPkResponse(PkCrypto.encrypt(modifiedMember.getMemberId()));
    }

    public void softDeleteMember(List<String> memberIdList) {
        memberIdList.forEach(memberId -> {
            Member findMember = memberGetService.byMemberId(PkCrypto.decrypt(memberId));

            validateAuthorization(findMember);

            deleteProfileImage(findMember.getProfileImage());

            findMember.modifyIsDeleted(true);

            memberUpdateService.modifyMemberInfo(findMember);

            redisService.deleteValues("MEMBER_INFO:" + findMember.getMemberId());
        });
    }

    private void deleteProfileImage(String profileImageUrl) {
        if (s3ApiService.deleteObject(profileImageUrl)) {
            log.error("Failed to delete profile image, profileImageUrl : {}", profileImageUrl);
        }
    }

    private void validateAuthorization(Member member) {
        if (member.getRole().equals(Role.ADMIN)) {
            throw ApplicationException.from(AuthErrorCode.PERMISSION_DENIED);
        }
    }

    private void validateEmailDuplicate(String memberEmail, String emailReq) {
        if (!memberEmail.equals(emailReq)) {
            memberFieldValidator.validateEmailDuplicate(emailReq);
        }
    }

    private void validateNicknameDuplicate(String memberNickname, String nicknameReq) {
        if (!memberNickname.equals(nicknameReq)) {
            memberFieldValidator.validateNicknameDuplicate(nicknameReq);
        }
    }

    public void validateImageFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.contains(".")) {
            throw ApplicationException.from(INVALID_IMAGE_EXTENSION);
        }

        String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        boolean isValid = false;

        for (String ext : ACCEPTED_EXTENSIONS) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw ApplicationException.from(INVALID_IMAGE_EXTENSION);
        }
    }

    public void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw ApplicationException.from(EXCEED_MAX_FILE_SIZE);
        }
    }

    public Boolean isFileUpload(MultipartFile file) {
        return (file == null || file.isEmpty()) ? false : true;
    }
}
