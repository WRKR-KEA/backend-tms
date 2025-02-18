package com.wrkr.tickety.domains.member.application.usecase;

import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_A;
import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_B;
import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_C;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoPreviewResponse;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.domain.constant.SortType;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("회원 전체 조회 및 검색 UseCase Layer Test")
public class MemberInfoSearchUseCaseTest {

    @InjectMocks
    private MemberInfoSearchUseCase memberInfoSearchUseCase;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private PkCrypto pkCrypto;

    @BeforeEach
    public void setUp() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }


    @Test
    @DisplayName("유효한 역할과 검색어로 회원 정보를 검색하면 성공한다.")
    public void searchMemberInfoSuccess() {
        // given
        ApplicationPageRequest pageRequest = new ApplicationPageRequest(0, 10, SortType.NEWEST);
        Role role = Role.USER;
        String query = "test";

        Member member1 = USER_A.toMember();
        Member member2 = USER_B.toMember();
        Member member3 = USER_C.toMember();

        List<Member> searchedMembers = List.of(member1, member2, member3);

        Page<Member> memberPage = new PageImpl<>(
            searchedMembers,
            PageRequest.of(0, 20),
            searchedMembers.size()
        );

        given(memberGetService.searchMember(any(ApplicationPageRequest.class), any(Role.class), any(String.class))).willReturn(memberPage);

        // when
        ApplicationPageResponse<MemberInfoPreviewResponse> response =
            memberInfoSearchUseCase.searchMemberInfo(pageRequest, role, query);

        // then
        assertThat(response).isNotNull();
        assertThat(response.totalElements()).isEqualTo(3);
        assertThat(response.elements()).hasSize(3);
    }

    @Test
    @DisplayName("필터링 기준을 ADMIN으로 요청 시 METHOD_ARGUMENT_NOT_VALID 예외를 발생시킨다.")
    public void searchMemberInfoWithAdminRoleThrowsException() {
        // given
        ApplicationPageRequest pageRequest = new ApplicationPageRequest(0, 10, SortType.NEWEST);
        Role role = Role.ADMIN;
        String query = "test";

        // when & then
        assertThatThrownBy(() -> memberInfoSearchUseCase.searchMemberInfo(pageRequest, role, query))
            .isInstanceOf(ApplicationException.class)
            .hasMessageContaining(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID.getMessage());
    }
}
