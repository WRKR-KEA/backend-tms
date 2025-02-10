package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.member.application.dto.request.MemberInfoUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberUpdateService;
import com.wrkr.tickety.domains.member.presentation.util.validator.MemberFieldValidator;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
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

    public MemberPkResponse modifyMemberInfo(String memberId, MemberInfoUpdateRequest memberInfoUpdateRequest, MultipartFile profileImage) {
        memberFieldValidator.validateField(
            memberInfoUpdateRequest.name(),
            memberInfoUpdateRequest.department(),
            memberInfoUpdateRequest.position(),
            memberInfoUpdateRequest.phone(),
            memberInfoUpdateRequest.role(),
            memberInfoUpdateRequest.nickname(),
            memberInfoUpdateRequest.email()
        );

        Member findMember = memberGetService.byMemberId(PkCrypto.decrypt(memberId));

        boolean isEmptyProfileImage = profileImage == null || profileImage.isEmpty();

        if (!isEmptyProfileImage) {
            s3ApiService.deleteObject(findMember.getProfileImage());
            String newProfileImageUrl = s3ApiService.uploadMemberProfileImage(profileImage);

            findMember.modifyProfileImage(newProfileImageUrl);
        }

        findMember.modifyMemberInfo(memberInfoUpdateRequest);
        Member modifiedMember = memberUpdateService.modifyMemberInfo(findMember);

        return MemberMapper.toMemberPkResponse(PkCrypto.encrypt(modifiedMember.getMemberId()));
    }

    public void softDeleteMember(List<String> memberIdList) {
        memberIdList.forEach(memberId -> {
            Member findMember = memberGetService.byMemberId(PkCrypto.decrypt(memberId));

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
}
