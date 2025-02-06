package com.wrkr.tickety.domains.log.presentation;

import com.wrkr.tickety.domains.log.application.dto.response.AccessLogSearchResponse;
import com.wrkr.tickety.domains.log.application.usecase.AccessLogSearchUseCase;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Log Controller")
public class LogController {

    private final AccessLogSearchUseCase accessLogSearchUseCase;

    @Operation(summary = "관리자 - 접속 로그 조회 및 검색(페이징)", description = "접속 로그 목록을 페이징으로 조회 및 검색합니다.")
    @CustomErrorCodes(commonErrorCodes = {CommonErrorCode.BAD_REQUEST})
    @Parameters({
        @Parameter(name = "role", description = "회원 역할 (USER | MANAGER | ADMIN)", example = "USER"),
        @Parameter(name = "query", description = "검색어 (닉네임 | ip)", example = "nickname | 111.234.567.89"),
        @Parameter(name = "pageRequest", description = "페이징", example = "{\"page\":1,\"size\":20}")
    })
    @GetMapping("/api/admin/access-logs")
    public ApplicationResponse<ApplicationPageResponse<AccessLogSearchResponse>> searchAccessLogs(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "페이징", example = "{\"page\":1,\"size\":20}")
        ApplicationPageRequest pageRequest,
        @RequestParam(required = false) Role role,
        @RequestParam(required = false) String query,
        @RequestParam(required = false) ActionType action
    ) {
        return ApplicationResponse.onSuccess(accessLogSearchUseCase.searchAccessLogs(pageRequest.toPageable(), role, query, action));
    }
}
