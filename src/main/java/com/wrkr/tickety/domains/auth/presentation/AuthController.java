package com.wrkr.tickety.domains.auth.presentation;

import static com.wrkr.tickety.domains.auth.exception.AuthErrorCode.ACCOUNT_LOCKED;
import static com.wrkr.tickety.domains.auth.exception.AuthErrorCode.INVALID_CREDENTIALS;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

import com.wrkr.tickety.domains.auth.application.dto.request.LoginRequest;
import com.wrkr.tickety.domains.auth.application.dto.response.TokenResponse;
import com.wrkr.tickety.domains.auth.application.usecase.AuthTokenUseCase;
import com.wrkr.tickety.domains.auth.application.usecase.LoginUseCase;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final AuthTokenUseCase authTokenUseCase;

    @PostMapping("/login")
    @CustomErrorCodes(memberErrorCodes = {MEMBER_NOT_FOUND}, authErrorCodes = {INVALID_CREDENTIALS, ACCOUNT_LOCKED})
    @Operation(summary = "로그인", description = "회원이 닉네임과 비밀번호로 로그인을 합니다.")
    public ApplicationResponse<TokenResponse> login(@RequestBody LoginRequest request) {
        Member member = loginUseCase.login(request);
        TokenResponse response = authTokenUseCase.generateToken(member);
        return ApplicationResponse.onSuccess(response);
    }
}
