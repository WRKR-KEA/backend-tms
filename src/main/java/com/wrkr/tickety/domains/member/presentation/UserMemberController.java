package com.wrkr.tickety.domains.member.presentation;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_PASSWORD_FORMAT;

import com.wrkr.tickety.domains.member.application.dto.request.PasswordReissueRequest;
import com.wrkr.tickety.domains.member.application.dto.request.PasswordUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.usecase.PasswordReissueUseCase;
import com.wrkr.tickety.domains.member.application.usecase.PasswordUpdateUseCase;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/members")
@Tag(name = "UserMember Controller")
public class UserMemberController {

    private final PasswordUpdateUseCase passwordUpdateUseCase;
    private final PasswordReissueUseCase passwordReissueUseCase;


    @Operation(summary = "비밀번호 재설정", description = "사용자 또는 담당자가 비밀번호 재설정을 진행합니다.")
    @CustomErrorCodes(memberErrorCodes = {INVALID_PASSWORD_FORMAT})
    @PatchMapping("/password")
    public ApplicationResponse<MemberPkResponse> updatePassword(
        @AuthenticationPrincipal Member member,
        @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest
    ) {
        MemberPkResponse memberPkResponse = passwordUpdateUseCase.updatePassword(member.getMemberId(), passwordUpdateRequest.password());
        return ApplicationResponse.onSuccess(memberPkResponse);
    }

    @Operation(summary = "비밀번호 재발급", description = "사용자 또는 담당자는 이메일로 임시 비밀번호를 발급받을 수 있습니다.")
    @CustomErrorCodes(memberErrorCodes = {INVALID_PASSWORD_FORMAT})
    @PatchMapping("/password/reissue")
    public ApplicationResponse<MemberPkResponse> regeneratePassword(
        @AuthenticationPrincipal Member member,
        @RequestBody @Valid PasswordReissueRequest passwordReissueRequest
    ) {
        MemberPkResponse memberPkResponse = passwordReissueUseCase.reissuePassword(passwordReissueRequest.nickname());
        return ApplicationResponse.onSuccess(memberPkResponse);
    }
}
