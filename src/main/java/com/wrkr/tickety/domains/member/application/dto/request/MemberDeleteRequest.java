package com.wrkr.tickety.domains.member.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Schema(description = "회원 삭제 요청 DTO")
@Builder
public record MemberDeleteRequest(
        @Schema(description = "삭제할 Member의 PK List", example = "[\"MJMzUD7jxcQfUiy3yPMl6A\", \"FlWOIqcZByZJmHE3p-VGEw\"]")
        @NotEmpty(message = "요청 값이 유효하지 않습니다.")
        List<String> memberIdList
) {
}
