package com.wrkr.tickety.domains.member.presentation;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_NICKNAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.DEPARTMENT_IS_BLANK;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.EMAIL_IS_BLANK;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_EMAIL_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_NICKNAME_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_PHONE_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_ROLE;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.NAME_IS_BLANK;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.NICKNAME_IS_BLANK;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.PHONE_IS_BLANK;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.POSITION_IS_BLANK;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.BAD_REQUEST;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.INTERNAL_SERVER_ERROR;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequestForExcel;
import com.wrkr.tickety.domains.member.application.dto.request.MemberDeleteRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberInfoUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.usecase.ExcelExampleCreateUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberCreateFromExcelUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberCreateUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoGetUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoSearchUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoUpdateUseCase;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.annotation.file.FileExtension;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.excel.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
@Tag(name = "Admin Member Controller")
@Validated
public class AdminMemberController {

    private final MemberCreateFromExcelUseCase memberCreateFromExcelUseCase;
    private final MemberCreateUseCase memberCreateUseCase;
    private final MemberInfoUpdateUseCase memberInfoUpdateUseCase;
    private final MemberInfoGetUseCase memberInfoGetUseCase;
    private final MemberInfoSearchUseCase memberInfoSearchUseCase;
    private final ExcelExampleCreateUseCase excelExampleCreateUseCase;
    private final ExcelUtil excelUtil;

    @CustomErrorCodes(memberErrorCodes =
        {
            EMAIL_IS_BLANK, INVALID_EMAIL_FORMAT, ALREADY_EXIST_EMAIL,
            NAME_IS_BLANK,
            NICKNAME_IS_BLANK, INVALID_NICKNAME_FORMAT, ALREADY_EXIST_NICKNAME,
            DEPARTMENT_IS_BLANK,
            POSITION_IS_BLANK,
            PHONE_IS_BLANK, INVALID_PHONE_FORMAT,
            INVALID_ROLE
        }
    )
    @Parameter(
        name = "memberCreateRequest",
        description = "회원 등록 요청 데이터",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = MemberCreateRequest.class)
        )
    )
    @Operation(summary = "관리자 - 회원 등록", description = "회원을 등록 시 이메일로 임시 비밀번호를 발급합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationResponse<MemberPkResponse> createMember(
        @AuthenticationPrincipal Member member,
        @RequestPart MemberCreateRequest memberCreateRequest,
        @RequestPart MultipartFile profileImage
    ) {
        MemberPkResponse memberPkResponse = memberCreateUseCase.createMember(memberCreateRequest, profileImage);

        return ApplicationResponse.onSuccess(memberPkResponse);
    }

    // TODO: csv 파일도 업로드할 수 있도록 개선 가능
    @CustomErrorCodes(memberErrorCodes =
        {
            EMAIL_IS_BLANK, INVALID_EMAIL_FORMAT, ALREADY_EXIST_EMAIL,
            NAME_IS_BLANK,
            NICKNAME_IS_BLANK, INVALID_NICKNAME_FORMAT, ALREADY_EXIST_NICKNAME,
            DEPARTMENT_IS_BLANK,
            POSITION_IS_BLANK,
            PHONE_IS_BLANK, INVALID_PHONE_FORMAT,
            INVALID_ROLE
        }
    )
    @Operation(summary = "관리자 - 회원 등록(엑셀 파일 업로드)", description = "정해진 양식의 엑셀 파일 내용을 읽어 회원을 등록합니다.")
    @PostMapping(value = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationResponse<List<MemberPkResponse>> createMemberExcelUpload(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "엑셀 파일")
        @RequestParam @FileExtension(acceptedExtensions = {"xls", "xlsx"}) @NotNull(message = "업로드할 파일을 선택해주세요.") MultipartFile file
    ) {
        List<MemberCreateRequestForExcel> memberCreateRequestForExcels = excelUtil.parseExcelToObject(file, MemberCreateRequestForExcel.class);
        List<MemberPkResponse> memberPkResponses = memberCreateFromExcelUseCase.createMember(memberCreateRequestForExcels);

        return ApplicationResponse.onSuccess(memberPkResponses);
    }

    @Operation(summary = "관리자 - 회원 등록 엑셀 양식 다운로드", description = "회원 등록에 필요한 정해진 양식의 엑셀 파일을 다운로드합니다.")
    @CustomErrorCodes(commonErrorCodes = INTERNAL_SERVER_ERROR)
    @GetMapping(value = "/excel/example")
    public void createMemberExcelExampleDownload(
        HttpServletResponse response,
        @AuthenticationPrincipal Member member
    ) {
        List<MemberCreateRequestForExcel> memberInfoExamples = excelExampleCreateUseCase.createMemberInfoExample();
        excelUtil.renderObjectToExcel(response, memberInfoExamples, MemberCreateRequestForExcel.class, "member_info_example");
    }

    @CustomErrorCodes(memberErrorCodes =
        {
            MEMBER_NOT_FOUND,
            EMAIL_IS_BLANK, INVALID_EMAIL_FORMAT, ALREADY_EXIST_EMAIL,
            NAME_IS_BLANK,
            NICKNAME_IS_BLANK, INVALID_NICKNAME_FORMAT, ALREADY_EXIST_NICKNAME,
            DEPARTMENT_IS_BLANK,
            POSITION_IS_BLANK,
            PHONE_IS_BLANK, INVALID_PHONE_FORMAT,
            INVALID_ROLE
        }
    )
    @Operation(summary = "관리자 - 회원 정보 수정", description = "회원 정보를 수정합니다.")
    @PatchMapping(value = "/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Parameter(
        name = "memberInfoUpdateRequest",
        description = "회원 수정 요청 데이터",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = MemberInfoUpdateRequest.class)
        )
    )
    public ApplicationResponse<MemberPkResponse> modifyMemberInfo(
        @AuthenticationPrincipal Member member,
        @PathVariable String memberId,
        @RequestPart MemberInfoUpdateRequest memberInfoUpdateRequest,
        @RequestPart MultipartFile profileImage
    ) {
        MemberPkResponse memberPkResponse = memberInfoUpdateUseCase.modifyMemberInfo(memberId, memberInfoUpdateRequest, profileImage);
        return ApplicationResponse.onSuccess(memberPkResponse);
    }

    @CustomErrorCodes(memberErrorCodes = {MEMBER_NOT_FOUND})
    @Operation(summary = "관리자 - 선택한 회원 삭제", description = "선택한 회원을 일괄 삭제합니다.")
    @DeleteMapping
    public ApplicationResponse<Void> softDeleteMember(
        @AuthenticationPrincipal Member member,
        @RequestBody MemberDeleteRequest memberDeleteRequest
    ) {
        memberInfoUpdateUseCase.softDeleteMember(memberDeleteRequest.memberIdList());
        return ApplicationResponse.onSuccess();
    }

    @CustomErrorCodes(memberErrorCodes = {MemberErrorCode.MEMBER_NOT_FOUND})
    @Operation(summary = "관리자 - 회원 정보 상세조회", description = "선택한 회원의 상세 정보를 조회합니다.")
    @GetMapping("/{memberId}")
    public ApplicationResponse<MemberInfoResponse> getMemberInfo(@PathVariable String memberId) {
        MemberInfoResponse memberInfoDTO = memberInfoGetUseCase.getMemberInfo(memberId);
        return ApplicationResponse.onSuccess(memberInfoDTO);
    }

    // TODO: ConstraintViolationException 제대로 처리되지 않는 문제 해결 필요
    @CustomErrorCodes(commonErrorCodes = {BAD_REQUEST})
    @Operation(summary = "관리자 - 회원 정보 목록 조회 및 검색(페이징)", description = "회원 정보 목록을 페이징으로 조회합니다.")
    @Parameters({
        @Parameter(name = "role", description = "회원 역할 (USER | MANAGER | ADMIN)", example = "USER"),
        @Parameter(name = "query", description = "검색어 (이메일, 이름, 부서)", example = "alsgudtkwjs@gachon.ac.kr(이메일) or 김가천(이름) or 부서(개발 1팀)"),
        @Parameter(name = "pageable", description = "페이징", example = "{\"page\":1,\"size\":20}")
    })
    @GetMapping
    public ApplicationResponse<ApplicationPageResponse<MemberInfoResponse>> getTotalMemberInfo(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "페이징", example = "{\"page\":1,\"size\":20}")
        ApplicationPageRequest pageRequest,
        @RequestParam(required = false) Role role,
        @RequestParam(required = false) String query
    ) {
        return ApplicationResponse.onSuccess(
            memberInfoSearchUseCase.searchMemberInfo(
                pageRequest,
                role,
                query
            )
        );
    }
}
