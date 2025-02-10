package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequestForExcel;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
public class ExcelExampleCreateUseCase {

    public List<MemberCreateRequestForExcel> createMemberInfoExample() {
        List<MemberCreateRequestForExcel> exDatas = new ArrayList<>();

        MemberCreateRequestForExcel exData1 = MemberCreateRequestForExcel.builder()
            .email("wrkr.kea@gachon.ac.kr")
            .name("김가천")
            .nickname("gachon.kim")
            .department("인프라 지원팀")
            .position("네트워크 엔지니어")
            .phone("010-1234-5678")
            .role(Role.MANAGER)
            .profileImage("https://search.pstatic.net/sunny/?src=https%3A%2F%2Fimage.flaticon.com%2Ficons%2Fpng%2F512%2F74%2F74472.png&type=a340")
            .agitUrl("https://example.com/agit")
            .build();

        MemberCreateRequestForExcel exData2 = MemberCreateRequestForExcel.builder()
            .email("gcgy@gachon.ac.kr")
            .name("김길여")
            .nickname("gilyu.kim")
            .department("인사 지원팀")
            .position("인사 담당자")
            .phone("010-1234-5678")
            .role(Role.USER)
            .profileImage("https://search.pstatic.net/sunny/?src=https%3A%2F%2Fimage.flaticon.com%2Ficons%2Fpng%2F512%2F74%2F74472.png&type=a340")
            .agitUrl("https://example.com/agit")
            .build();

        exDatas.add(exData1);
        exDatas.add(exData2);

        return exDatas;
    }

}
