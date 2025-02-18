package com.wrkr.tickety.domains.member.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequestForExcel;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExcelExampleCreateUseCaseTest {

    @InjectMocks
    private ExcelExampleCreateUseCase excelExampleCreateUseCase;

    @Test
    @DisplayName("성공: 회원 생성 예제 데이터를 반환한다.")
    void createMemberInfoExample_Success() {
        // When
        List<MemberCreateRequestForExcel> result = excelExampleCreateUseCase.createMemberInfoExample();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        MemberCreateRequestForExcel firstMember = result.get(0);
        MemberCreateRequestForExcel secondMember = result.get(1);

        assertAll(
            () -> assertThat(firstMember.getEmail()).isEqualTo("wrkr.kea@gachon.ac.kr"),
            () -> assertThat(firstMember.getName()).isEqualTo("김가천"),
            () -> assertThat(firstMember.getNickname()).isEqualTo("gachon.kim"),
            () -> assertThat(firstMember.getDepartment()).isEqualTo("인프라 지원팀"),
            () -> assertThat(firstMember.getPosition()).isEqualTo("네트워크 엔지니어"),
            () -> assertThat(firstMember.getPhone()).isEqualTo("010-1234-5678"),
            () -> assertThat(firstMember.getRole()).isEqualTo(Role.MANAGER),
            () -> assertThat(firstMember.getProfileImage()).isEqualTo("https://i.ibb.co/7Fd4Hhx/tickety-default-image.jpg"),
            () -> assertThat(firstMember.getAgitUrl()).isEqualTo("https://example.com/agit")
        );

        assertAll(
            () -> assertThat(secondMember.getEmail()).isEqualTo("gcgy@gachon.ac.kr"),
            () -> assertThat(secondMember.getName()).isEqualTo("김길여"),
            () -> assertThat(secondMember.getNickname()).isEqualTo("gilyu.kim"),
            () -> assertThat(secondMember.getDepartment()).isEqualTo("인사 지원팀"),
            () -> assertThat(secondMember.getPosition()).isEqualTo("인사 담당자"),
            () -> assertThat(secondMember.getPhone()).isEqualTo("010-1234-5678"),
            () -> assertThat(secondMember.getRole()).isEqualTo(Role.USER),
            () -> assertThat(secondMember.getProfileImage()).isEqualTo("https://i.ibb.co/7Fd4Hhx/tickety-default-image.jpg"),
            () -> assertThat(secondMember.getAgitUrl()).isEqualTo("https://example.com/agit")
        );
    }
}
