package com.wrkr.tickety.domains.member.presentation;

import static com.wrkr.tickety.domains.auth.exception.AuthErrorCode.INVALID_VERIFICATION_CODE;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_NICKNAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

import com.wrkr.tickety.domains.member.application.dto.request.PasswordReissueRequest;
import com.wrkr.tickety.domains.member.application.dto.request.VerificationCodeRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.usecase.PasswordReissueUseCase;
import com.wrkr.tickety.domains.member.application.usecase.VerificationCodeCreateUseCase;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Public Controller")
public class PublicController {

    private final PasswordReissueUseCase passwordReissueUseCase;
    private final VerificationCodeCreateUseCase verificationCodeCreateUseCase;

    @Operation(summary = "비밀번호 재발급", description = "사용자 또는 담당자는 올바른 인증 코드를 입력하면 임시 비밀번호를 발급받을 수 있습니다.")
    @CustomErrorCodes(
        memberErrorCodes = {MEMBER_NOT_FOUND},
        authErrorCodes = {INVALID_VERIFICATION_CODE}
    )
    @PatchMapping("/api/members/password/reissue")
    public ApplicationResponse<MemberPkResponse> regeneratePassword(
        @RequestBody PasswordReissueRequest passwordReissueRequest
    ) {
        MemberPkResponse memberPkResponse = passwordReissueUseCase.reissuePassword(passwordReissueRequest.memberId(),
            passwordReissueRequest.verificationCode());
        return ApplicationResponse.onSuccess(memberPkResponse);
    }

    @Operation(summary = "인증 코드 발급", description = "비밀번호 재발급 전, 인증 코드를 발급합니다.")
    @CustomErrorCodes(memberErrorCodes = {INVALID_NICKNAME})
    @PostMapping("/api/members/password/code")
    public ApplicationResponse<MemberPkResponse> createVerifyCode(
        @RequestBody VerificationCodeRequest verificationCodeRequest
    ) {
        return ApplicationResponse.onSuccess(verificationCodeCreateUseCase.createVerificationCode(verificationCodeRequest.nickname()));
    }


}
