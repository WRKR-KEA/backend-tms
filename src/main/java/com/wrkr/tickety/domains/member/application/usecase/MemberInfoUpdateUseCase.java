package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.auth.exception.AuthErrorCode;
import com.wrkr.tickety.domains.member.application.dto.request.MemberInfoUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberUpdateService;
import com.wrkr.tickety.domains.member.presentation.util.validator.MemberFieldValidator;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
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

    private final MemberUpdateService memberUpdateService;
    private final MemberGetService memberGetService;
    private final S3ApiService s3ApiService;
    private final MemberFieldValidator memberFieldValidator;

    public MemberPkResponse modifyMemberInfo(String memberId, MemberInfoUpdateRequest request, MultipartFile profileImage) {
        Member findMember = memberGetService.byMemberId(PkCrypto.decrypt(memberId));

        validateAuthorization(findMember);
        validateField(findMember, request.name(), request.department(), request.position(), request.phone(), request.role(), request.nickname(),
            request.email());

        boolean isEmptyProfileImage = profileImage == null || profileImage.isEmpty();

        if (!isEmptyProfileImage) {
            s3ApiService.deleteObject(findMember.getProfileImage());
            String newProfileImageUrl = s3ApiService.uploadMemberProfileImage(profileImage);

            findMember.modifyProfileImage(newProfileImageUrl);
        }

        findMember.modifyMemberInfo(request);
        Member modifiedMember = memberUpdateService.modifyMemberInfo(findMember);

        return MemberMapper.toMemberPkResponse(PkCrypto.encrypt(modifiedMember.getMemberId()));
    }

    public void softDeleteMember(List<String> memberIdList) {
        memberIdList.forEach(memberId -> {
            Member findMember = memberGetService.byMemberId(PkCrypto.decrypt(memberId));

            validateAuthorization(findMember);

            findMember.modifyIsDeleted(true);
            Member modifiedMember = memberUpdateService.modifyMemberInfo(findMember);

            deleteProfileImage(findMember.getProfileImage());

            memberUpdateService.modifyMemberInfo(modifiedMember);
        });
    }

    private void deleteProfileImage(String profileImageUrl) {
        if (s3ApiService.deleteProfileImage(profileImageUrl)) {
            log.error("Failed to delete profile image, profileImageUrl : {}", profileImageUrl);
        }
    }

    // 다른 관리자의 정보 수정 금지
    private void validateAuthorization(Member member) {
        if (member.getRole().equals(Role.ADMIN)) {
            throw ApplicationException.from(AuthErrorCode.PERMISSION_DENIED);
        }
    }

    // 요청 값의 닉네임, 이메일이 기존 닉네임, 이메일과 같은 경우 중복이 일어나지 않도록 처리
    private void validateField(Member findMember, String name, String department, String position, String phone, Role role, String nickname, String email) {
        memberFieldValidator.validateName(name);
        memberFieldValidator.validateDepartment(department);
        memberFieldValidator.validatePosition(position);
        memberFieldValidator.validatePhoneFormat(phone);
        memberFieldValidator.validateRole(role);

        if (!findMember.getNickname().equals(nickname)) {
            memberFieldValidator.validateNicknameDuplicate(nickname);
        }

        if (!findMember.getEmail().equals(email)) {
            memberFieldValidator.validateEmailDuplicate(email);
        }
    }
}
