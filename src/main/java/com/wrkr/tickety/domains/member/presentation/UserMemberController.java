package com.wrkr.tickety.domains.member.presentation;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_PASSWORD_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.UNMATCHED_PASSWORD;

import com.wrkr.tickety.domains.member.application.dto.request.PasswordUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
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

    @Operation(summary = "비밀번호 재설정", description = "사용자 또는 담당자가 비밀번호 재설정을 진행합니다.")
    @CustomErrorCodes(memberErrorCodes = {INVALID_PASSWORD_FORMAT, UNMATCHED_PASSWORD})
    @PatchMapping("/password")
    public ApplicationResponse<MemberPkResponse> updatePassword(
        @AuthenticationPrincipal Member member,
        @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest
    ) {
        MemberPkResponse memberPkResponse = passwordUpdateUseCase.updatePassword(member.getMemberId(), passwordUpdateRequest.password(),
            passwordUpdateRequest.confirmPassword());
        return ApplicationResponse.onSuccess(memberPkResponse);
    }
}
