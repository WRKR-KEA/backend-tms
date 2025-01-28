package com.wrkr.tickety.domains.ticket.application.dto.response.ticket;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "티켓 상세 조회 응답 DTO", name = "티켓 상세 조회 응답")
public record ManagerTicketDetailResponse(

    @Schema(description = "티켓 id", example = "Bqs3C822lkMNdWlmE-szUw")
    String ticketId,

    @Schema(description = "티켓 식별번호", example = "VM001")
    String ticketSerialNumber,

    @Schema(description = "티켓 제목", example = "VM 생성 요청")
    String title,

    @Schema(description = "티켓 내용", example = """
        ### VM 기본 사항
        - 이름: project-app-server01
        - 운영 체제: Ubuntu 20.04 LTS
        ### VM 사양
        - CPU: 4 cores
        - 메모리: 16GB
        - 스토리지: 100GB SSD
        - 기타:
        ### 네트워크
        - 허용 포트: 22, 8080
        ### 프로젝트
        - 연관 프로젝트: Project A
        - 예상 운영 기간: 1개월
        ### 요청 사유
        - 애플리케이션 테스트를 위해 필요합니다.
        
        ---
        
        ### 기타 사항
        1주일 내로 처리 부탁드립니다.""")
    String content,

    @Schema(description = "업무", example = "VM 추가")
    String category,

    @Schema(description = "사용자 닉네임", example = "request.er")
    String userNickname,

    @Schema(description = "담당자 닉네임", example = "manage.r")
    String managerNickname,

    @Schema(description = "생성 일시", example = "2021-01-01T00:00:00")
    String createdAt,

    @Schema(description = "수정 일시", example = "2021-01-01T00:00:00")
    String updatedAt,

    @Schema(description = "시작 일시", example = "2021-01-01T00:00:00")
    String startedAt,

    @Schema(description = "종료 일시", example = "null")
    String completedAt,

    @Schema(description = "티켓 상태", example = "REQUESTED")
    TicketStatus status
) {

}
