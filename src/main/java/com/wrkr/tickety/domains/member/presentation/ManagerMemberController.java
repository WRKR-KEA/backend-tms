package com.wrkr.tickety.domains.member.presentation;

import com.wrkr.tickety.domains.member.application.dto.response.ManagerGetAllManagerResponse;
import com.wrkr.tickety.domains.member.application.usecase.ManagerGetAllManagersUseCase;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/members")
@Tag(name = "Manager Member Controller")
public class ManagerMemberController {

    private final ManagerGetAllManagersUseCase managerGetAllManagersUseCase;

    @Operation(summary = "담당자 - 모든 담당자 조회", description = "모든 담당자를 조회합니다.")
    @CustomErrorCodes({})
    @GetMapping
    public ApplicationResponse<ManagerGetAllManagerResponse> getAllManagers() {
        ManagerGetAllManagerResponse response = managerGetAllManagersUseCase.getAllManagers();
        return ApplicationResponse.onSuccess(response);
    }

    @Operation(summary = "담당자 - 모든 담당자 조회(페이징)", description = "모든 담당자를 페이징하여 조회합니다.")
    @CustomErrorCodes({})
    @GetMapping("/page")
    public ApplicationResponse<ApplicationPageResponse<ManagerGetAllManagerResponse.Managers>> getAllPageManagers(
        @Parameter(description = "페이징", example = "{\"page\":1,\"size\":20}")
        ApplicationPageRequest pageRequest
    ) {
        ApplicationPageResponse<ManagerGetAllManagerResponse.Managers> response = managerGetAllManagersUseCase.getAllPageManagers(pageRequest);
        return ApplicationResponse.onSuccess(response);
    }

}
