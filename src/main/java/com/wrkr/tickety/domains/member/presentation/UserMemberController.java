package com.wrkr.tickety.domains.member.presentation;

import com.wrkr.tickety.domains.member.application.dto.request.PasswordUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.usecase.PasswordUpdateUseCase;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/members")
@Tag(name = "UserMember Controller")
public class UserMemberController {

    private final PasswordUpdateUseCase passwordUpdateUseCase;

    @PatchMapping
    public ApplicationResponse<MemberPkResponse> updatePassword(
        @AuthenticationPrincipal Member member,
        @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest
    ) {
        MemberPkResponse memberPkResponse = passwordUpdateUseCase.updatePassword(member.getMemberId(), passwordUpdateRequest.password());
        return ApplicationResponse.onSuccess(memberPkResponse);
    }
}
