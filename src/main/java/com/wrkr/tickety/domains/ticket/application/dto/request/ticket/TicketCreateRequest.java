package com.wrkr.tickety.domains.ticket.application.dto.request.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "티켓 생성 요청 DTO", name = "티켓 생성 요청")
public record TicketCreateRequest(

    @Schema(description = "티켓 제목", example = "티켓 제목")
    @NotBlank(message = "티켓 제목은 필수 항목입니다.")
    @Size(max = 50, message = "제목은 50자 이하로 입력해 주세요.")
    String title,

    @Schema(description = "티켓 내용", example = "티켓 내용")
    @NotBlank(message = "티켓 내용은 필수 항목입니다.")
    String content,

    @Schema(description = "2차 카테고리", example = "카테고리 ID")
    @NotNull(message = "2차 카테고리는 필수 항목입니다.")
    String categoryId

) {

}
