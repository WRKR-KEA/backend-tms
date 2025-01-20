package com.wrkr.tickety.domains.member.presentation;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateReqDTO;
import com.wrkr.tickety.domains.member.application.dto.response.MemberCreateResDTO;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResDTO;
import com.wrkr.tickety.domains.member.application.usecase.MemberCreateUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberGetUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberCreateUseCase memberCreateUseCase;
    private final MemberGetUseCase memberGetUseCase;

    // TODO: Image 처리 방식 논의 필요(Multipart or ImageUrl DTO로 바로 받기)
    @PostMapping("/admin/members")
    public ApplicationResponse<MemberCreateResDTO> createMember(@RequestBody @Valid MemberCreateReqDTO reqDTO) {
        MemberCreateResDTO memberIdDTO = memberCreateUseCase.createMember(reqDTO);
        return ApplicationResponse.onSuccess(memberIdDTO);
    }

    @GetMapping("/admin/members/{memberId}")
    public ApplicationResponse<MemberInfoResDTO> getMemberInfo(String memberId) {
        MemberInfoResDTO memberInfoDTO = memberGetUseCase.getMemberInfo(memberId);

        return ApplicationResponse.onSuccess(memberInfoDTO);
    }
}
