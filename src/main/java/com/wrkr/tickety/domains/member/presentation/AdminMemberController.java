package com.wrkr.tickety.domains.member.presentation;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberDeleteRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.usecase.MemberCreateUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberGetUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberUpdateUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
@Tag(name = "Admin Member Controller")
public class AdminMemberController {
    private final MemberCreateUseCase memberCreateUseCase;
    private final MemberUpdateUseCase memberUpdateUseCase;
    private final MemberGetUseCase memberGetUseCase;

    // TODO: 해당 컨트롤러 모든 API에 헤더 토큰으로 요청한 회원 PK 추출 및 권한이 관리자인지 검증 필요
    // TODO: Image 처리 방식 논의 필요(Multipart or ImageUrl DTO로 바로 받기)
    @Operation(summary = "관리자 - 회원 등록", description = "회원을 등록 시 이메일로 임시 비밀번호를 발급합니다.")
//    @CustomErrorCodes({MemberErrorCode.INVALID_EMAIL_FORMAT, MemberErrorCode.ALREADY_EXIST_EMAIL, MemberErrorCode.INVALID_PHONE_FORMAT, MemberErrorCode.INVALID_ROLE})
    @PostMapping
    public ApplicationResponse<MemberPkResponse> createMember(@RequestBody @Valid MemberCreateRequest memberCreateRequest) {
        MemberPkResponse memberIdDTO = memberCreateUseCase.createMember(memberCreateRequest);
        return ApplicationResponse.onSuccess(memberIdDTO);
    }

    @Operation(summary = "관리자 - 회원 정보 수정", description = "회원 정보를 수정합니다.")
//    @CustomErrorCodes({MemberErrorCode.MEMBER_NOT_FOUND, MemberErrorCode.INVALID_EMAIL_FORMAT, MemberErrorCode.INVALID_PHONE_FORMAT, MemberErrorCode.INVALID_ROLE})
    @PatchMapping("/{memberId}")
    public ApplicationResponse<MemberPkResponse> modifyMemberInfo(@PathVariable String memberId, @RequestBody @Valid MemberUpdateRequest memberUpdateRequest) {
        MemberPkResponse memberPkResponse = memberUpdateUseCase.modifyMemberInfo(memberId, memberUpdateRequest);
        return ApplicationResponse.onSuccess(memberPkResponse);
    }

    @Operation(summary = "관리자 - 선택한 회원 삭제", description = "선택한 회원을 일괄 삭제합니다.")
//    @CustomErrorCodes({MemberErrorCode.MEMBER_NOT_FOUND})
    @DeleteMapping
    public ApplicationResponse<Void> softDeleteMember(@RequestBody MemberDeleteRequest memberDeleteRequest) {
        memberUpdateUseCase.softDeleteMember(memberDeleteRequest.memberIdList());
        return ApplicationResponse.onSuccess();
    }

    @Operation(summary = "관리자 - 회원 정보 상세조회", description = "선택한 회원의 상세 정보를 조회합니다.")
//    @CustomErrorCodes({MemberErrorCode.MEMBER_NOT_FOUND})
    @GetMapping("/{memberId}")
    public ApplicationResponse<MemberInfoResponse> getMemberInfo(@PathVariable String memberId) {
        MemberInfoResponse memberInfoDTO = memberGetUseCase.getMemberInfo(memberId);
        return ApplicationResponse.onSuccess(memberInfoDTO);
    }
}
