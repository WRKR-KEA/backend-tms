package com.wrkr.tickety.common.fixture.ticket;

import com.wrkr.tickety.common.fixture.member.UserFixture;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import lombok.Getter;

@Getter
public enum TicketFixture {

    // 요청 상태 (REQUEST)
    TICKET_REQUEST_01(101L, "#250202VMCRE01", "서버 장애 발생", "서버가 응답하지 않습니다.", TicketStatus.REQUEST, false, UserFixture.MANAGER_C, UserFixture.USER_B),
    TICKET_REQUEST_02(102L, "#250202VMCRE02", "DB 성능 저하", "쿼리 실행 속도가 느립니다.", TicketStatus.REQUEST, false, UserFixture.MANAGER_C, UserFixture.USER_D),
    TICKET_REQUEST_03(103L, "#250202VMCRE03", "네트워크 장애", "VPN 연결이 끊어졌습니다.", TicketStatus.REQUEST, true, UserFixture.MANAGER_C, UserFixture.USER_F),
    TICKET_REQUEST_04(104L, "#250202VMCRE04", "권한 요청", "DB 접근 권한 요청", TicketStatus.REQUEST, false, UserFixture.MANAGER_C, UserFixture.USER_H),
    TICKET_REQUEST_05(105L, "#250202VMCRE05", "서버 재시작 요청", "배포 후 서버가 다운되었습니다.", TicketStatus.REQUEST, false, UserFixture.MANAGER_C, UserFixture.USER_A),
    TICKET_REQUEST_06(106L, "#250202VMCRE06", "로그 파일 요청", "서버 로그 파일이 필요합니다.", TicketStatus.REQUEST, false, UserFixture.MANAGER_C, UserFixture.USER_C),
    TICKET_REQUEST_07(107L, "#250202VMCRE07", "네트워크 설정 변경", "VPN 설정을 변경해야 합니다.", TicketStatus.REQUEST, false, UserFixture.MANAGER_C, UserFixture.USER_E),
    TICKET_REQUEST_08(108L, "#250202VMCRE08", "모니터링 추가 요청", "Grafana 대시보드 추가 요청.", TicketStatus.REQUEST, false, UserFixture.MANAGER_A, UserFixture.USER_G),
    TICKET_REQUEST_09(109L, "#250202VMCRE09", "스토리지 용량 부족", "NAS 저장소 공간 부족", TicketStatus.REQUEST, false, UserFixture.MANAGER_A, UserFixture.USER_I),
    TICKET_REQUEST_10(110L, "#250202VMCRE10", "백업 설정 변경", "자동 백업 주기 변경 요청", TicketStatus.REQUEST, false, UserFixture.MANAGER_A, UserFixture.USER_B),

    // 진행 상태 (IN_PROGRESS)
    TICKET_IN_PROGRESS_01(201L, "#250202DBCRE01", "서버 유지보수 진행 중", "정기 점검 수행 중", TicketStatus.IN_PROGRESS, false, UserFixture.MANAGER_A, UserFixture.USER_E),
    TICKET_IN_PROGRESS_02(202L, "#250202DBCRE02", "DB 마이그레이션 진행", "데이터 이전 작업 중", TicketStatus.IN_PROGRESS, false, UserFixture.MANAGER_A, UserFixture.USER_G),
    TICKET_IN_PROGRESS_03(203L, "#250202DBCRE03", "보안 패치 적용", "보안 업데이트 수행 중", TicketStatus.IN_PROGRESS, false, UserFixture.MANAGER_A, UserFixture.USER_I),
    TICKET_IN_PROGRESS_04(204L, "#250202DBCRE04", "신규 기능 테스트", "테스트 환경에서 기능 검증", TicketStatus.IN_PROGRESS, false, UserFixture.MANAGER_A, UserFixture.USER_B),
    TICKET_IN_PROGRESS_05(205L, "#250202DBCRE05", "데이터 정리 작업", "DB 테이블 최적화 진행", TicketStatus.IN_PROGRESS, false, UserFixture.MANAGER_A, UserFixture.USER_D),
    TICKET_IN_PROGRESS_06(206L, "#250202DBCRE06", "서버 설정 변경", "서버 설정을 최적화합니다.", TicketStatus.IN_PROGRESS, false, UserFixture.MANAGER_C, UserFixture.USER_F),
    TICKET_IN_PROGRESS_07(207L, "#250202DBCRE07", "애플리케이션 배포", "새로운 버전 배포 진행 중", TicketStatus.IN_PROGRESS, false, UserFixture.MANAGER_B, UserFixture.USER_G),
    TICKET_IN_PROGRESS_08(208L, "#250202DBCRE08", "보안 점검 진행", "보안 검토 중", TicketStatus.IN_PROGRESS, false, UserFixture.MANAGER_C, UserFixture.USER_H),
    TICKET_IN_PROGRESS_09(209L, "#250202DBCRE09", "로그 분석", "애플리케이션 로그 분석 중", TicketStatus.IN_PROGRESS, false, UserFixture.MANAGER_C, UserFixture.USER_I),
    TICKET_IN_PROGRESS_10(210L, "#250202DBCRE10", "데이터베이스 최적화", "DB 성능 개선 진행 중", TicketStatus.IN_PROGRESS, false, UserFixture.MANAGER_D, UserFixture.USER_A),

    // 완료 상태 (COMPLETE)
    TICKET_COMPLETE_01(301L, "#250202DBCRE01", "서버 재부팅 완료", "서버가 정상적으로 재부팅되었습니다.", TicketStatus.COMPLETE, true, UserFixture.MANAGER_A, UserFixture.USER_B),
    TICKET_COMPLETE_02(302L, "#250202DBCRE02", "네트워크 구성 완료", "VPN 설정이 변경되었습니다.", TicketStatus.COMPLETE, false, UserFixture.MANAGER_A, UserFixture.USER_D),
    TICKET_COMPLETE_03(303L, "#250202DBCRE03", "DB 백업 완료", "데이터베이스 백업이 완료되었습니다.", TicketStatus.COMPLETE, false, UserFixture.MANAGER_A, UserFixture.USER_F),

    // 취소 상태 (CANCEL)
    TICKET_CANCEL_01(401L, "#250202MNETC01", "서버 점검 요청 취소", "점검이 필요하지 않음", TicketStatus.CANCEL, false, UserFixture.MANAGER_D, UserFixture.USER_H),
    TICKET_CANCEL_02(402L, "#250202MNETC02", "네트워크 변경 요청 취소", "기존 설정 유지하기로 함", TicketStatus.CANCEL, false, UserFixture.MANAGER_D, UserFixture.USER_A),

    // 반려 상태 (REJECT)
    TICKET_REJECT_01(501L, "#250202FWCRE01", "불필요한 요청 반려", "요청 사유가 불충분함", TicketStatus.REJECT, false, UserFixture.MANAGER_C, UserFixture.USER_C),
    TICKET_REJECT_02(502L, "#250202FWCRE02", "권한 요청 반려", "승인 거절됨", TicketStatus.REJECT, false, UserFixture.MANAGER_C, UserFixture.USER_E);

    private final Long ticketId;
    private final String serialNumber;
    private final String title;
    private final String content;
    private final TicketStatus status;
    private final Boolean isPinned;
    private final UserFixture manager;
    private final UserFixture user;

    TicketFixture(Long ticketId, String serialNumber, String title, String content, TicketStatus status, Boolean isPinned, UserFixture manager,
        UserFixture user) {
        this.ticketId = ticketId;
        this.serialNumber = serialNumber;
        this.title = title;
        this.content = content;
        this.status = status;
        this.isPinned = isPinned;
        this.manager = manager;
        this.user = user;
    }

    public Ticket toTicket() {
        return Ticket.builder()
            .ticketId(this.ticketId)
            .serialNumber(this.serialNumber)
            .title(this.title)
            .content(this.content)
            .status(this.status)
            .isPinned(this.isPinned)
            .manager(this.manager.toMember())
            .user(this.user.toMember())
            .build();
    }
}