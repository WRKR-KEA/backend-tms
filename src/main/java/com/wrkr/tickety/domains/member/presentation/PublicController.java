package com.wrkr.tickety.domains.member.presentation;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

import com.wrkr.tickety.domains.member.application.dto.request.PasswordReissueRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.usecase.PasswordReissueUseCase;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Public Controller")
public class PublicController {

    private final PasswordReissueUseCase passwordReissueUseCase;

    @Operation(summary = "비밀번호 재발급", description = "사용자 또는 담당자는 이메일로 임시 비밀번호를 발급받을 수 있습니다.")
    @CustomErrorCodes(memberErrorCodes = {MEMBER_NOT_FOUND /*, INVALID_NICKNAME_FORMAT*/})
    @PatchMapping("/api/members/password/reissue")
    public ApplicationResponse<MemberPkResponse> regeneratePassword(
        @RequestBody @Valid PasswordReissueRequest passwordReissueRequest
    ) {
        MemberPkResponse memberPkResponse = passwordReissueUseCase.reissuePassword(passwordReissueRequest.nickname());
        return ApplicationResponse.onSuccess(memberPkResponse);
    }
}
