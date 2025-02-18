package com.wrkr.tickety.domains.member.presentation;

import com.wrkr.tickety.domains.member.application.dto.response.ManagerGetAllManagerResponse;
import com.wrkr.tickety.domains.member.application.usecase.ManagerGetAllManagersUseCase;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @GetMapping
    public ApplicationResponse<ManagerGetAllManagerResponse> getAllManagers(
        @AuthenticationPrincipal Member member
    ) {
        ManagerGetAllManagerResponse response = managerGetAllManagersUseCase.getAllManagers(member);
        return ApplicationResponse.onSuccess(response);
    }
}
