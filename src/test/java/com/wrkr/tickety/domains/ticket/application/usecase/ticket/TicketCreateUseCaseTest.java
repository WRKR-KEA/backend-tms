package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistorySaveService;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketCreateUseCaseTest {

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    private static final Long USER_ID = 1L;
    private static final Long CATEGORY_ID = 2L;

    private Member user;
    private Category childCategory;
    private Category parentCategory;
    private TicketCreateRequest validRequest;
    private String encryptedCategoryId;

    @Mock
    private TicketSaveService ticketSaveService;

    @Mock
    private CategoryGetService categoryGetService;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private TicketHistorySaveService ticketHistorySaveService;

    @Mock
    private TicketGetService ticketGetService;

    @InjectMocks
    private TicketCreateUseCase ticketCreateUseCase;

    @BeforeEach
    void setUp() {
        encryptedCategoryId = PkCrypto.encrypt(CATEGORY_ID);

        user = Member.builder()
            .memberId(USER_ID)
            .nickname("사용자")
            .email("user@naver.com")
            .isDeleted(false)
            .build();

        parentCategory = Category.builder()
            .categoryId(1L)
            .name("부모 카테고리")
            .abbreviation("PC") // 여기 추가
            .build();

        childCategory = Category.builder()
            .categoryId(CATEGORY_ID)
            .name("카테고리")
            .parent(parentCategory)
            .build();

        validRequest = TicketCreateRequest.builder()
            .title("새로운 티켓")
            .content("티켓 내용입니다.")
            .categoryId(encryptedCategoryId)
            .build();
    }

    @Test
    @DisplayName("성공: 티켓을 정상적으로 생성")
    void createTicket_Success() {
        // given
        Ticket ticket = Ticket.builder()
            .ticketId(1L)
            .user(user)
            .title(validRequest.title())
            .content(validRequest.content())
            .category(childCategory)
            .status(TicketStatus.REQUEST)
            .build();

        given(categoryGetService.getChildrenCategory(CATEGORY_ID)).willReturn(childCategory);
        given(memberGetService.byMemberId(USER_ID)).willReturn(user);
        given(ticketSaveService.save(any(Ticket.class))).willReturn(ticket);
        given(ticketGetService.findLastSequence(any(), any())).willReturn("01");

        // when
        TicketPkResponse response = ticketCreateUseCase.createTicket(validRequest, USER_ID);

        // then
        assertThat(response.ticketId()).isNotNull();
    }

    @Test
    @DisplayName("실패: 존재하지 않는 카테고리 ID로 생성 시 예외 발생")
    void createTicket_CategoryNotFound() {
        // given
        given(categoryGetService.getChildrenCategory(CATEGORY_ID))
            .willThrow(new ApplicationException(CategoryErrorCode.CATEGORY_NOT_EXISTS));

        // when & then
        assertThatThrownBy(() -> ticketCreateUseCase.createTicket(validRequest, USER_ID))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(CategoryErrorCode.CATEGORY_NOT_EXISTS.getMessage());
    }

    @Test
    @DisplayName("실패: 존재하지 않는 사용자 ID로 생성 시 예외 발생")
    void createTicket_UserNotFound() {
        // given
        given(categoryGetService.getChildrenCategory(CATEGORY_ID)).willReturn(childCategory);
        given(memberGetService.byMemberId(USER_ID))
            .willThrow(new ApplicationException(MemberErrorCode.MEMBER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> ticketCreateUseCase.createTicket(validRequest, USER_ID))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }
}

