package com.wrkr.tickety.domains.member.presentation;

import com.wrkr.tickety.domains.member.application.dto.request.MyPageInfoUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.application.usecase.MyPageInfoGetUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MyPageInfoUpdateUseCase;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/my-page")
@Tag(name = "MyPage Controller")
public class MyPageController {

    private final MyPageInfoGetUseCase myPageInfoGetUseCase;
    private final MyPageInfoUpdateUseCase myPageInfoUpdateUseCase;

    @Operation(summary = "사용자/담당자 - 마이페이지 회원 정보 조회", description = "사용자/담당자 마이페이지 회원 정보를 조회합니다.")
    @GetMapping
    @CustomErrorCodes(memberErrorCodes = {MemberErrorCode.MEMBER_NOT_FOUND, MemberErrorCode.DELETED_MEMBER})
    public ApplicationResponse<MyPageInfoResponse> getMemberInfo(
        @AuthenticationPrincipal Member member
    ) {
        MyPageInfoResponse response = myPageInfoGetUseCase.getMyPageInfo(member.getMemberId());
        return ApplicationResponse.onSuccess(response);
    }

    @Operation(summary = "사용자/담당자 - 마이페이지 회원 정보 수정", description = "사용자/담당자 마이페이지 회원 정보를 조회합니다.")
    @PatchMapping
    @CustomErrorCodes(memberErrorCodes = {MemberErrorCode.MEMBER_NOT_FOUND, MemberErrorCode.DELETED_MEMBER})
    public ApplicationResponse<MemberPkResponse> updateMemberInfo(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "회원 정보 수정 요청 정보", required = true)
        @Valid @RequestBody MyPageInfoUpdateRequest request
    ) {
        MemberPkResponse response = myPageInfoUpdateUseCase.updateMyPageInfo(member.getMemberId(), request);
        return ApplicationResponse.onSuccess(response);
    }
}
