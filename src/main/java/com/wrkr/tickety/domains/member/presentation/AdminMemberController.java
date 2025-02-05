package com.wrkr.tickety.domains.member.presentation;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_EMAIL_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_PHONE_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_ROLE;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberDeleteRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.usecase.MemberCreateUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoGetUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoSearchUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoUpdateUseCase;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.common.dto.PageResponse;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
@Tag(name = "Admin Member Controller")
@Validated
public class AdminMemberController {

    private final MemberCreateUseCase memberCreateUseCase;
    private final MemberInfoUpdateUseCase memberInfoUpdateUseCase;
    private final MemberInfoGetUseCase memberInfoGetUseCase;
    private final MemberInfoSearchUseCase memberInfoSearchUseCase;

    // TODO: Image 처리 방식 논의 필요(Multipart or ImageUrl DTO로 바로 받기)
    @Operation(summary = "관리자 - 회원 등록", description = "회원을 등록 시 이메일로 임시 비밀번호를 발급합니다.")
    @CustomErrorCodes(memberErrorCodes = {INVALID_EMAIL_FORMAT, ALREADY_EXIST_EMAIL, INVALID_PHONE_FORMAT, INVALID_ROLE})
    @PostMapping
    public ApplicationResponse<MemberPkResponse> createMember(
        @AuthenticationPrincipal Member member,
        @RequestBody @Valid MemberCreateRequest memberCreateRequest
    ) {
        MemberPkResponse memberIdDTO = memberCreateUseCase.createMember(memberCreateRequest);
        return ApplicationResponse.onSuccess(memberIdDTO);
    }

    @Operation(summary = "관리자 - 회원 정보 수정", description = "회원 정보를 수정합니다.")
    @CustomErrorCodes(memberErrorCodes = {MEMBER_NOT_FOUND, INVALID_EMAIL_FORMAT, INVALID_PHONE_FORMAT, INVALID_ROLE})
    @PatchMapping("/{memberId}")
    public ApplicationResponse<MemberPkResponse> modifyMemberInfo(
        @AuthenticationPrincipal Member member,
        @PathVariable String memberId,
        @RequestBody @Valid MemberUpdateRequest memberUpdateRequest
    ) {
        MemberPkResponse memberPkResponse = memberInfoUpdateUseCase.modifyMemberInfo(memberId, memberUpdateRequest);
        return ApplicationResponse.onSuccess(memberPkResponse);
    }

    @Operation(summary = "관리자 - 선택한 회원 삭제", description = "선택한 회원을 일괄 삭제합니다.")
    @CustomErrorCodes(memberErrorCodes = {MEMBER_NOT_FOUND})
    @DeleteMapping
    public ApplicationResponse<Void> softDeleteMember(
        @AuthenticationPrincipal Member member,
        @RequestBody MemberDeleteRequest memberDeleteRequest
    ) {
        memberInfoUpdateUseCase.softDeleteMember(memberDeleteRequest.memberIdList());
        return ApplicationResponse.onSuccess();
    }

    @Operation(summary = "관리자 - 회원 정보 상세조회", description = "선택한 회원의 상세 정보를 조회합니다.")
    @CustomErrorCodes(memberErrorCodes = {MemberErrorCode.MEMBER_NOT_FOUND})
    @GetMapping("/{memberId}")
    public ApplicationResponse<MemberInfoResponse> getMemberInfo(@PathVariable String memberId) {
        MemberInfoResponse memberInfoDTO = memberInfoGetUseCase.getMemberInfo(memberId);
        return ApplicationResponse.onSuccess(memberInfoDTO);
    }

    // TODO: ConstraintViolationException 제대로 처리되지 않는 문제 해결 필요, 페이징 공통 응답 클래스 이용
    @Operation(summary = "관리자 - 회원 정보 목록 조회 및 검색(페이징)", description = "회원 정보 목록을 페이징으로 조회합니다.")
    @CustomErrorCodes(commonErrorCodes = {CommonErrorCode.BAD_REQUEST})
    @Parameters({
        @Parameter(name = "role", description = "회원 역할 (USER | MANAGER | ADMIN)", example = "USER"),
        @Parameter(description = "검색어 (이메일, 이름, 부서)", example = "alsgudtkwjs@gachon.ac.kr(이메일) or 김가천(이름) or 부서(개발 1팀)"),
        @Parameter(description = "페이징", example = "{\"page\":1,\"size\":20}")
    })
    @GetMapping
    public ApplicationResponse<PageResponse<MemberInfoResponse>> getTotalMemberInfo(
        @AuthenticationPrincipal Member member,
        @RequestParam(required = false) Role role,
        @RequestParam(required = false) String query,
        Pageable pageable
    ) {
        return ApplicationResponse.onSuccess(
            memberInfoSearchUseCase.searchMemberInfo(
                role,
                query,
                pageable
            )
        );
    }
}
