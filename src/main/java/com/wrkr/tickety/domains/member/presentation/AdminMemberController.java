package com.wrkr.tickety.domains.member.presentation;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_EMAIL_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_PHONE_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_ROLE;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberDeleteRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoPagingResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.usecase.MemberCreateUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoGetUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberUpdateUseCase;
import com.wrkr.tickety.domains.member.application.usecase.TotalMemberInfoGetUseCase;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
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
    private final MemberUpdateUseCase memberUpdateUseCase;
    private final MemberInfoGetUseCase memberInfoGetUseCase;
    private final TotalMemberInfoGetUseCase totalMemberInfoGetUseCase;

    // TODO: 해당 컨트롤러 모든 API에 헤더 토큰으로 요청한 회원 PK 추출 및 권한이 관리자인지 검증 필요
    // TODO: Image 처리 방식 논의 필요(Multipart or ImageUrl DTO로 바로 받기)
    @Operation(summary = "관리자 - 회원 등록", description = "회원을 등록 시 이메일로 임시 비밀번호를 발급합니다.")
    @CustomErrorCodes(memberErrorCodes = {INVALID_EMAIL_FORMAT, ALREADY_EXIST_EMAIL, INVALID_PHONE_FORMAT, INVALID_ROLE})
    @PostMapping
    public ApplicationResponse<MemberPkResponse> createMember(@RequestBody @Valid MemberCreateRequest memberCreateRequest) {
        MemberPkResponse memberIdDTO = memberCreateUseCase.createMember(memberCreateRequest);
        return ApplicationResponse.onSuccess(memberIdDTO);
    }

    @Operation(summary = "관리자 - 회원 정보 수정", description = "회원 정보를 수정합니다.")
    @CustomErrorCodes(memberErrorCodes = {MEMBER_NOT_FOUND, INVALID_EMAIL_FORMAT, INVALID_PHONE_FORMAT, INVALID_ROLE})
    @PatchMapping("/{memberId}")
    public ApplicationResponse<MemberPkResponse> modifyMemberInfo(@PathVariable String memberId, @RequestBody @Valid MemberUpdateRequest memberUpdateRequest) {
        MemberPkResponse memberPkResponse = memberUpdateUseCase.modifyMemberInfo(memberId, memberUpdateRequest);
        return ApplicationResponse.onSuccess(memberPkResponse);
    }

    @Operation(summary = "관리자 - 선택한 회원 삭제", description = "선택한 회원을 일괄 삭제합니다.")
    @CustomErrorCodes(memberErrorCodes = {MEMBER_NOT_FOUND})
    @DeleteMapping
    public ApplicationResponse<Void> softDeleteMember(@RequestBody MemberDeleteRequest memberDeleteRequest) {
        memberUpdateUseCase.softDeleteMember(memberDeleteRequest.memberIdList());
        return ApplicationResponse.onSuccess();
    }

    @Operation(summary = "관리자 - 회원 정보 상세조회", description = "선택한 회원의 상세 정보를 조회합니다.")
    @CustomErrorCodes(memberErrorCodes = {MemberErrorCode.MEMBER_NOT_FOUND})
    @GetMapping("/{memberId}")
    public ApplicationResponse<MemberInfoResponse> getMemberInfo(@PathVariable String memberId) {
        MemberInfoResponse memberInfoDTO = memberInfoGetUseCase.getMemberInfo(memberId);
        return ApplicationResponse.onSuccess(memberInfoDTO);
    }

    // TODO: ConstraintViolationException 제대로 처리되지 않는 문제 해결 필요
    @Operation(summary = "관리자 - 회원 정보 목록 조회 및 검색(페이징)", description = "회원 정보 목록을 페이징으로 조회합니다. page는 1 이상, size는 10 이상이어야 합니다")
    @GetMapping
    public ApplicationResponse<MemberInfoPagingResponse> getTotalMemberInfo(
        @RequestParam(defaultValue = "0") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") int page,
        @RequestParam(defaultValue = "10") @Min(value = 10, message = "페이지 크기는 10 이상이어야 합니다.") int size,
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String department
    ) {
        return ApplicationResponse.onSuccess(totalMemberInfoGetUseCase.searchMemberInfo(
            page - 1,
            size,
            role,
            email,
            name,
            department
        ));
    }

    @Operation(summary = "관리자 - 회원 정보 목록 조회(페이징)", description = "전체 회원을 페이징으로 조회합니다. page는 1 이상, size는 10 이상이어야 합니다")
    @GetMapping("/search")
    public ApplicationResponse<Void> getTotalMemberInfo() {
        return ApplicationResponse.onSuccess();
    }
}
